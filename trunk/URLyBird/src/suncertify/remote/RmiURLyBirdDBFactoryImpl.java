package suncertify.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiURLyBirdDBFactoryImpl extends UnicastRemoteObject implements
		RmiURLyBirdDBFactory {

	private static final long serialVersionUID = 1L;
	
	private String dbFileLocation = null;
	
	protected RmiURLyBirdDBFactoryImpl(String dbFileLocation) throws RemoteException {
		this.dbFileLocation = dbFileLocation;
	}

	@Override
	public RmiURLyBirdDB getURLyBirdDB() throws RemoteException {
		return new RmiURLyBirdDBImpl(dbFileLocation);
	}
}
