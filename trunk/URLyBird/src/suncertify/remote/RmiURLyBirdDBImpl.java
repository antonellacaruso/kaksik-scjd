package suncertify.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import suncertify.db.Data;
import suncertify.db.URLyBirdDB;
import suncertify.db.exception.DuplicateKeyException;
import suncertify.db.exception.RecordNotFoundException;

public class RmiURLyBirdDBImpl extends UnicastRemoteObject implements
		RmiURLyBirdDB {
	private static final long serialVersionUID = 1L;

	URLyBirdDB database = null;

	protected RmiURLyBirdDBImpl(String dbLocation) throws RemoteException {
		database = new Data(dbLocation);
	}

	@Override
	public String[] getHeaders() {
		return database.getHeaders();
	}

	@Override
	public int create(String[] data) throws DuplicateKeyException {
		return database.create(data);
	}

	@Override
	public void delete(int recNo, long lockCookie) throws SecurityException,
			RecordNotFoundException {
		database.delete(recNo, lockCookie);
	}

	@Override
	public int[] find(String[] criteria) {
		return database.find(criteria);
	}

	@Override
	public long lock(int recNo) throws RecordNotFoundException {
		return database.lock(recNo);
	}

	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		return database.read(recNo);
	}

	@Override
	public void unlock(int recNo, long cookie) throws RecordNotFoundException,
			SecurityException {
		database.unlock(recNo, cookie);
	}

	@Override
	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		database.update(recNo, data, lockCookie);
	}
}
