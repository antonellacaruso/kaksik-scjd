package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Äkki peaks faili sulgema ka mingi hetk?
 * 
 */
public class DataHelper {
	private final RandomAccessFile dbFile;

	// Header name, length in bytes
	private final LinkedHashMap<String, Byte> headerData = new LinkedHashMap<String, Byte>();

	// ordered
	private final Map<Integer, Long> filePointers = new LinkedHashMap<Integer, Long>();

	private final Map<Integer, Long> deletedRecordFilePointers = new LinkedHashMap<Integer, Long>();

	private final static byte FLAG_DELETED = (byte) 0xFF;
	private final static byte FLAG_EXISTING_RECORD = (byte) 0x0;

	private int newRecordNumber;

	private long newRecordFilePointer;

	public DataHelper(String dbFilePath) throws IOException {
		dbFile = new RandomAccessFile(dbFilePath, "rw");
		initMetaData();
	}

	// private List<String[]> getAllHotelRooms() throws IOException,
	// SecurityException, IllegalArgumentException, NoSuchFieldException,
	// IllegalAccessException {
	//
	// List<String[]> result = new ArrayList<String[]>();
	//
	// for (Long filePointer : filePointers.values()) {
	// result.add(getHotelRoomFromFile(filePointer));
	// }
	//
	// return result;
	// }

	public String[] getHotelRoom(int recordNo) throws IOException {
		long filePointer = filePointers.get(recordNo);
		return getHotelRoomFromFile(filePointer);
	}

	private String[] getHotelRoomFromFile(long filePointer) throws IOException {
		int nrOfFields = headerData.keySet().size();
		String[] result = new String[nrOfFields];

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

	public void deleteHotelRoom(int recordNo, long cookie) throws IOException,
			RecordNotFoundException {
		// TODO: catch exception if record is not found
		// Remove from file pointers
		if (!filePointers.containsKey(recordNo)) {
			throw new RecordNotFoundException();
		}
		long filePointer = filePointers.remove(recordNo);
		dbFile.seek(filePointer);
		dbFile.write(FLAG_DELETED);
		deletedRecordFilePointers.put(recordNo, filePointer);
	}

	// TODO: Move this into separate class
	public static int byteArrayToInt(byte[] byteArray) {
		int highBit = byteArray[0] & 0xff;
		int lowBit = byteArray[1] & 0xff;
		return (highBit << 8 | lowBit);
	}

	private void initMetaData() throws IOException {
		dbFile.seek(0);

		byte[] magicCookieValue = new byte[4];
		dbFile.readFully(magicCookieValue);

		byte[] nrOfFieldsInRecord = new byte[2];
		dbFile.readFully(nrOfFieldsInRecord);

		int nrOfFieldsInt = byteArrayToInt(nrOfFieldsInRecord);

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
			if (recordDeletedFlag == FLAG_DELETED) {
				deletedRecordFilePointers.put(recordNumber++, filePointer);
			} else {
				filePointers.put(recordNumber++, filePointer);
			}

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
			if (!deletedRecordFilePointers.isEmpty()) {
				recNo = deletedRecordFilePointers.keySet().iterator().next();
				filePointer = deletedRecordFilePointers.get(recNo);
			} else {
				recNo = newRecordNumber;
				filePointer = newRecordFilePointer;
			}
		} else {
			if (!filePointers.containsKey(recNo)) {
				throw new RecordNotFoundException();
			}
			filePointer = filePointers.get(recNo);
		}

		StringBuilder newRecord = new StringBuilder();
		newRecord.append(FLAG_EXISTING_RECORD);
		int dataIndex = 0;
		for (byte fieldLength : headerData.values()) {
			StringBuilder fieldValue = new StringBuilder(data[dataIndex++]);
			padWithSpaces(fieldValue, fieldLength);
			newRecord.append(fieldValue);
		}

		dbFile.seek(filePointer);
		dbFile.write(newRecord.toString().getBytes());

		if (isNewRecord && deletedRecordFilePointers.containsKey(recNo)) {
			// Removing from deleted pointers
			filePointers.put(recNo, deletedRecordFilePointers.remove(recNo));

		}

		return recNo;
	}

	public String[] getHeaders() {
		String[] headers = headerData.keySet().toArray(new String[] {});
		return headers;
	}

	public int[] find(String[] criteria) {
		List<Integer> results = new ArrayList<Integer>();
		// results.addAll(filePointers.keySet());
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

		int[] resultArray = new int[results.size()];
		for (int i = 0; i < results.size(); i++) {
			resultArray[i] = results.get(i);
		}

		return resultArray;
	}

	private static void padWithSpaces(StringBuilder value, int length) {
		int initialLength = value.length();
		value.setLength(length);
		if (initialLength < length) {
			for (int i = initialLength; i < length; i++) {
				value.setCharAt(i, ' '); // Space
			}
		}
	}

	public static void main(String[] args) throws IOException,
			SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, RecordNotFoundException {

		// DataHelper dbHelper = new DataHelper(
		// "C:\\java\\workspace\\URLyBird\\db-1x3.db");
		//
		// // I'm adding this row to test google SVN.
		//
		// long cookie = 0;
		// // dbHelper.deleteHotelRoom(1, cookie);
		//
		// String[] newRecord = { "New Hotel 6", "Lendmarch", "6", "Y",
		// "$170.00",
		// "2005/03/10", "" };
		// // dbHelper.saveHotelRoom(null, newRecord, cookie);
		//
		// List<String[]> hotelRooms = dbHelper.getAllHotelRooms();
		//
		// for (String[] hotelRoom : hotelRooms) {
		// for (String field : hotelRoom) {
		// System.out.print(field + ";");
		// }
		// System.out.println();
		// }
	}
}
