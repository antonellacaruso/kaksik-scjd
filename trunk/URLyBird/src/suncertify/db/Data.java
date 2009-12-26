package suncertify.db;

import java.io.IOException;

public class Data implements URLyBirdDB {

	DataHelper dataHelper = null;

	public Data(String dbFilePath) {
		try {
			dataHelper = new DataHelper(dbFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int create(String[] data) throws DuplicateKeyException {
		try {
			return dataHelper.saveHotelRoom(null, data);
		} catch (RecordNotFoundException e) {
			// Will never happen.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public void delete(int recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			dataHelper.deleteHotelRoom(recNo, lockCookie);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int[] find(String[] criteria) {
		return dataHelper.find(criteria);
	}

	public long lock(int recNo) throws RecordNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] read(int recNo) throws RecordNotFoundException {
		try {
			return dataHelper.getHotelRoom(recNo);
		} catch (IOException e) {
			// TODO: Maybe not the best idea...
			throw new RecordNotFoundException();
		}
	}

	public void unlock(int recNo, long cookie) throws RecordNotFoundException,
			SecurityException {
		// TODO Auto-generated method stub

	}

	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			dataHelper.saveHotelRoom(recNo, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RecordNotFoundException();
		}
	}

	public String[] getHeaders() {
		return dataHelper.getHeaders();
	}
}
