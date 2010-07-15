package suncertify.gui;

import suncertify.db.URLyBirdDBFactory;
import suncertify.db.Data;
import suncertify.db.HotelRoom;
import suncertify.db.URLyBirdDB;
import suncertify.db.exception.RecordNotFoundException;

public class GuiController {
	URLyBirdDB database = null;

	public GuiController(String dbUri) {
		database = URLyBirdDBFactory.getURLyBirdDB(dbUri);
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
				resultTableModel.addHotelRoomRecord(recordNumber, hotelRoom);
			}
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}

		return resultTableModel;
	}

	public HotelRoom read(int recNo) throws GuiControllerException {
		try {
			String[] hotelRoomStringArray = database.read(recNo);
			return HotelRoom.convertHotelRoomObject(hotelRoomStringArray);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		}
	}

	public void book(int recNo, String ownerId) throws GuiControllerException {
		Long cookie = null;
		try {
			cookie = database.lock(recNo);
			String[] hotelRoomStringArray = database.read(recNo);
			HotelRoom hotelRoom = HotelRoom
					.convertHotelRoomObject(hotelRoomStringArray);
			hotelRoom.setOwner(ownerId);
			database.update(recNo, hotelRoom.getStringArray(), cookie);
		} catch (SecurityException e) {
			throw new GuiControllerException(e);
		} catch (RecordNotFoundException e) {
			throw new GuiControllerException(e);
		} finally {
			// If cookie exists then record was successfully locked. Therefore
			// record must be unlocked.
			if (cookie != null) {
				try {
					database.unlock(recNo, cookie);
				} catch (SecurityException e) {
					// TODO Log exception. No need to re-throw.
					e.printStackTrace();
				} catch (RecordNotFoundException e) {
					// TODO Log exception. No need to re-throw.
					e.printStackTrace();
				}
			}
		}
	}
}
