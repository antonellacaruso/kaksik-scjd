package suncertify.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class HotelRoomTableModel extends AbstractTableModel {
	private final String[] headers;
	private final List<String[]> hotelRooms = new ArrayList<String[]>();

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

	public void addHotelRoomRecord(String[] hotelRoom) {
		hotelRooms.add(hotelRoom);
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}
}
