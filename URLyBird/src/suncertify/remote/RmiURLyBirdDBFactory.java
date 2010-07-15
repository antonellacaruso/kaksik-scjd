package suncertify.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiURLyBirdDBFactory extends Remote {
	RmiURLyBirdDB getURLyBirdDB() throws RemoteException;
}
