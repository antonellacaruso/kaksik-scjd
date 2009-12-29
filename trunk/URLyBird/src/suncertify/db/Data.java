package suncertify.db;

import java.io.IOException;

import suncertify.db.exception.DataPersistException;
import suncertify.db.exception.DuplicateKeyException;
import suncertify.db.exception.RecordNotFoundException;

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
			// This should never happen.
			throw new DataPersistException("Could not save data.", e);
		} catch (IOException e) {
			throw new DataPersistException("Could not save data.", e);
		}
	}

	public void delete(int recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			// Throws RecordNotFoundException or SecurityException if record
			// cannot be updated.
			HotelRoomLocker.ensureLock(recNo, lockCookie);

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
		// TODO: Currently ignoring RecordNotFoundException. Comment this
		// behavior.
		return HotelRoomLocker.lockHotelRoom(recNo);
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

		// If given record is not locked, then RecordNotFoundException is
		// thrown. If cookie is invalid, SecurityException is thrown.
		HotelRoomLocker.unlockHotelRoom(recNo, cookie);
	}

	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {

			// Throws RecordNotFoundException or SecurityException if record
			// cannot be updated.
			HotelRoomLocker.ensureLock(recNo, lockCookie);

			dataHelper.saveHotelRoom(recNo, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RecordNotFoundException(
					"Record could not be read. Record does probably not exist.",
					e);
		}
	}

	public String[] getHeaders() {
		return dataHelper.getHeaders();
	}
}
