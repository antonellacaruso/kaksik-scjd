package suncertify.gui;

import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.URLyBirdDB;

public class GuiController {
	URLyBirdDB database = null;

	public GuiController(String dbFilePath) {
		database = new Data(dbFilePath);
	}

	public int create(String[] data) throws GuiControllerException {
		try {
			return database.create(data);
		} catch (DuplicateKeyException e) {
			throw new GuiControllerException(e);
		}
	}

	public void delete(int recNo, long lockCookie)
			throws GuiControllerException {
		try {
			database.delete(recNo, lockCookie);
		} catch (SecurityException e) {
			throw new GuiControllerException(e);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}
	}

	public HotelRoomTableModel find(String searchString)
			throws GuiControllerException {
		String[] headers = database.getHeaders();
		HotelRoomTableModel resultTableModel = new HotelRoomTableModel(headers);

		String[] criteria = new String[headers.length];
		for (int i = 0; i < criteria.length; i++) {
			criteria[i] = searchString;
		}

		int[] recordNumbers = database.find(criteria);
		try {
			for (int recordNumber : recordNumbers) {
				String[] hotelRoom = database.read(recordNumber);
				resultTableModel.addHotelRoomRecord(hotelRoom);
			}
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}

		return resultTableModel;
	}

	public long lock(int recNo) throws GuiControllerException {
		try {
			return database.lock(recNo);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}
	}

	public String[] read(int recNo) throws GuiControllerException {
		try {
			return database.read(recNo);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}
	}

	public void unlock(int recNo, long cookie) throws GuiControllerException {
		try {
			database.unlock(recNo, cookie);
		} catch (SecurityException e) {
			throw new GuiControllerException(e);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}
	}

	public void update(int recNo, String[] data, long lockCookie)
			throws GuiControllerException {
		try {
			database.update(recNo, data, lockCookie);
		} catch (SecurityException e) {
			throw new GuiControllerException(e);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}
	}

}
