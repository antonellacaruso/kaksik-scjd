package suncertify.local;

import suncertify.db.DB;
import suncertify.db.Data;

public class Connector {
	public static DB getDB(String dbLocation) {
		return new Data(dbLocation);
	}
}
