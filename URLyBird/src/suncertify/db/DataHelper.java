package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import suncertify.db.exception.RecordNotFoundException;
import suncertify.util.ByteUtils;
import suncertify.util.StringUtils;

/**
 * TODO: Maybe I should close dbFile at some moment?
 * 
 */
public class DataHelper {
	private final RandomAccessFile dbFile;

	// Header name, length in bytes
	private final LinkedHashMap<String, Byte> headerData = new LinkedHashMap<String, Byte>();

	// ordered
	private final Map<Integer, Long> filePointers = new LinkedHashMap<Integer, Long>();

	private final Map<Integer, Long> deletedRecordFilePointers = new LinkedHashMap<Integer, Long>();

	private static ReadWriteLock filePointersLock = new ReentrantReadWriteLock();
	private static ReadWriteLock deletedRecordFilePointersLock = new ReentrantReadWriteLock();

	private final static byte FLAG_DELETED = (byte) 0xFF;
	private final static byte FLAG_EXISTING_RECORD = (byte) 0x0;

	private int newRecordNumber;

	private long newRecordFilePointer;

	public DataHelper(String dbFilePath) throws IOException {
		// No need to synchronize access to the dbFile in constructor.
		dbFile = new RandomAccessFile(dbFilePath, "rw");
		initMetaData();
	}

	public String[] getHotelRoom(int recordNo) throws IOException {
		filePointersLock.readLock().lock();
		try {
			long filePointer = filePointers.get(recordNo);
			return getHotelRoomFromFile(filePointer);
		} finally {
			filePointersLock.readLock().unlock();
		}
	}

	private String[] getHotelRoomFromFile(long filePointer) throws IOException {
		int nrOfFields = headerData.keySet().size();
		String[] result = new String[nrOfFields];

		synchronized (dbFile) {
			dbFile.seek(filePointer);
			byte isDeleted = dbFile.readByte(); // Deleted?
			int fieldNumber = 0;

			for (String fieldName : headerData.keySet()) {
				byte fieldLength = headerData.get(fieldName);
				byte[] data = new byte[fieldLength];
				dbFile.readFully(data);
				result[fieldNumber++] = new String(data);
			}
			if (isDeleted == FLAG_DELETED) {
				return null;
			} else {
				return result;
			}
		}
	}

	public void deleteHotelRoom(int recordNo, long cookie) throws IOException,
			RecordNotFoundException {
		filePointersLock.writeLock().lock();
		// deletedRecordFilePointersLock
		Long filePointer = null;
		try {
			if (!filePointers.containsKey(recordNo)) {
				throw new RecordNotFoundException();
			}
			filePointer = filePointers.remove(recordNo);
		} finally {
			filePointersLock.writeLock().unlock();
		}
		synchronized (dbFile) {
			dbFile.seek(filePointer);
			dbFile.write(FLAG_DELETED);
		}

		deletedRecordFilePointersLock.writeLock().lock();
		deletedRecordFilePointers.put(recordNo, filePointer);
		deletedRecordFilePointersLock.writeLock().unlock();

	}

	// As long as this method is invoked by the constructor, no synchronization
	// and locking is needed.
	private void initMetaData() throws IOException {
		dbFile.seek(0);

		byte[] magicCookieValue = new byte[4];
		dbFile.readFully(magicCookieValue);

		// TODO validate magic cookie. (To check file validity).

		byte[] nrOfFieldsInRecord = new byte[2];
		dbFile.readFully(nrOfFieldsInRecord);

		int nrOfFieldsInt = ByteUtils.byteArrayToInt(nrOfFieldsInRecord);

		int recordLength = 0;

		for (int fieldNr = 0; fieldNr < nrOfFieldsInt; fieldNr++) {
			byte fieldNameLength = dbFile.readByte();
			byte[] fieldName = new byte[fieldNameLength];
			dbFile.readFully(fieldName);
			byte fieldLength = dbFile.readByte();

			// TODO: Encoding
			headerData.put(new String(fieldName), fieldLength);
			recordLength += fieldLength;
		}

		int recordNumber = 0;
		long filePointer = dbFile.getFilePointer();
		while (filePointer < dbFile.length()) {
			dbFile.seek(filePointer);

			// Initializing record numbers
			byte recordDeletedFlag = dbFile.readByte();
			// filePointersLock.writeLock().lock();
			// try {
			if (recordDeletedFlag == FLAG_DELETED) {
				deletedRecordFilePointers.put(recordNumber++, filePointer);
			} else {
				filePointers.put(recordNumber++, filePointer);
			}
			// } finally {
			// filePointersLock.writeLock().unlock();
			// }

			filePointer = dbFile.getFilePointer() + recordLength;
		}

		newRecordNumber = recordNumber++;
		newRecordFilePointer = filePointer;
	}

	public int saveHotelRoom(Integer recNo, String[] data)
			throws RecordNotFoundException, IOException {
		boolean isNewRecord = (recNo == null);
		Long filePointer = null;

		if (isNewRecord) {
			deletedRecordFilePointersLock.readLock().lock();
			if (!deletedRecordFilePointers.isEmpty()) {
				recNo = deletedRecordFilePointers.keySet().iterator().next();
				filePointer = deletedRecordFilePointers.get(recNo);
			} else {
				recNo = newRecordNumber;
				filePointer = newRecordFilePointer;
			}
			deletedRecordFilePointersLock.readLock().unlock();
		} else {
			filePointersLock.readLock().lock();
			try {
				if (!filePointers.containsKey(recNo)) {
					throw new RecordNotFoundException();
				}
				filePointer = filePointers.get(recNo);
			} finally {
				filePointersLock.readLock().unlock();
			}
		}

		StringBuilder newRecord = new StringBuilder();
		newRecord.append(FLAG_EXISTING_RECORD);
		int dataIndex = 0;
		for (byte fieldLength : headerData.values()) {
			StringBuilder fieldValue = new StringBuilder(data[dataIndex++]);
			StringUtils.padWithSpaces(fieldValue, fieldLength);
			newRecord.append(fieldValue);
		}

		synchronized (dbFile) {
			dbFile.seek(filePointer);
			dbFile.write(newRecord.toString().getBytes());
		}

		filePointersLock.writeLock().lock();
		deletedRecordFilePointersLock.writeLock().lock();
		try {
			if (isNewRecord && deletedRecordFilePointers.containsKey(recNo)) {
				// Removing from deleted pointers
				Long deletedFilePointer = deletedRecordFilePointers
						.remove(recNo);
				filePointers.put(recNo, deletedFilePointer);

			}
		} finally {
			deletedRecordFilePointersLock.writeLock().unlock();
			filePointersLock.writeLock().unlock();
		}

		return recNo;

	}

	public String[] getHeaders() {
		String[] headers = headerData.keySet().toArray(new String[] {});
		return headers;
	}

	public int[] find(String[] criteria) {
		List<Integer> results = new ArrayList<Integer>();

		filePointersLock.readLock().lock();
		try {
			for (Integer recordNumber : filePointers.keySet()) {
				try {
					if (criteria == null) {
						results.add(recordNumber);
						continue;
					}

					String[] hotelRoom = getHotelRoom(recordNumber);
					for (int i = 0; i < hotelRoom.length; i++) {
						if (criteria[i] == null
								|| hotelRoom[i].toLowerCase().startsWith(
										criteria[i].toLowerCase())) {
							results.add(recordNumber);
							break;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {
			filePointersLock.readLock().unlock();
		}

		int[] resultArray = new int[results.size()];
		for (int i = 0; i < results.size(); i++) {
			resultArray[i] = results.get(i);
		}

		return resultArray;
	}
}
