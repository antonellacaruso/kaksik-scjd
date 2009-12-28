package suncertify.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import suncertify.db.HotelRoom;

public class HotelRoomTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final String[] headers;
	private final List<String[]> hotelRooms = new ArrayList<String[]>();

	// Keeps JTable row numbers and actual hotel room record numbers in pairs
	private final Map<Integer, Integer> recordNumberMap = new HashMap<Integer, Integer>();

	public HotelRoomTableModel(String[] headers) {
		this.headers = headers;
	}

	public int getColumnCount() {
		return headers.length;
	}

	public int getRowCount() {
		return hotelRooms.size();
	}

	public Object getValueAt(int row, int column) {
		String[] hotelRoom = hotelRooms.get(row);
		String value = hotelRoom[column];

		return value;
	}

	public void addHotelRoomRecord(int recordNumber, String[] hotelRoom) {
		int newItemIndex = hotelRooms.size();
		hotelRooms.add(hotelRoom);
		recordNumberMap.put(newItemIndex, recordNumber);
	}

	public int getRecordNumber(int rowNumber) {
		return recordNumberMap.get(rowNumber);
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	public boolean isAvailable(int row) {
		String[] hotelRoomStringArray = hotelRooms.get(row);
		HotelRoom hotelRoomObject = HotelRoom
				.convertHotelRoomObject(hotelRoomStringArray);
		boolean isAvailable = !hotelRoomObject.hasOwner();
		return isAvailable;
	}
}
