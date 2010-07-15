package suncertify.remote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiURLyBirdDBRegistry {
	public static void register(String dbFileLocation, int rmiPort) throws RemoteException {
		Registry registry = LocateRegistry.createRegistry(rmiPort);
		//URLyBirdDB
		
		registry.rebind("URLyBirdDBFactory", new RmiURLyBirdDBFactoryImpl(dbFileLocation));
	}
}
