package suncertify.db;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.remote.RmiURLyBirdDB;
import suncertify.remote.RmiURLyBirdDBFactory;
import suncertify.remote.RmiURLyBirdDBAdapter;

public class URLyBirdDBFactory {
	public static URLyBirdDB getURLyBirdDB(String dbUri) {
		if (dbUri.startsWith("rmi://")) {
			URLyBirdDB database = null;
			try {
				RmiURLyBirdDBFactory factory = (RmiURLyBirdDBFactory) Naming.lookup(dbUri);
				RmiURLyBirdDB remoteDB = factory.getURLyBirdDB();
				return new RmiURLyBirdDBAdapter(remoteDB);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return database;
		} else {
			return new Data(dbUri);
		}
	}
}
