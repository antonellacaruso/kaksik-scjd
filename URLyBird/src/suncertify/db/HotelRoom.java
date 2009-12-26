package suncertify.db;

import java.util.logging.Logger;

/**
 * @author Raido
 * 
 */
public class HotelRoom {
	private String name;

	private String location;

	private String size;

	private String smoking;

	private String rate;

	private String date;

	private String owner;

	private int recordNo;

	private transient final Logger log = Logger.getLogger(HotelRoom.class
			.toString());

	public HotelRoom() {
		log.fine("Empty constructor called");
	}

	public HotelRoom(String name, String location, String size, String smoking,
			String rate, String date, String owner) {
		log.fine("Constructor called");
		this.name = name;
		this.location = location;
		this.size = size;
		this.smoking = smoking;
		this.rate = rate;
		this.date = date;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSmoking() {
		return smoking;
	}

	public void setSmoking(String smoking) {
		this.smoking = smoking;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
	}

	@Override
	public String toString() {
		String returnValue = "HotelRoom(" + name + ", " + location + ", "
				+ size + ", " + smoking + ", " + rate + ", " + date + ", "
				+ owner + ")";

		return returnValue;
	}

	public void setField(String fieldName, byte[] value) {
		try {
			this.getClass().getDeclaredField(fieldName).set(this,
					new String(value).trim());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
