package suncertify.remote;

import java.rmi.RemoteException;

import suncertify.db.URLyBirdDB;
import suncertify.db.exception.DuplicateKeyException;
import suncertify.db.exception.RecordNotFoundException;

public class RmiURLyBirdDBAdapter implements URLyBirdDB {

	private RmiURLyBirdDB remoteURLyBirdDB;

	public RmiURLyBirdDBAdapter(RmiURLyBirdDB remoteURLyBirdDB) {
		this.remoteURLyBirdDB = remoteURLyBirdDB;
	}

	@Override
	public String[] getHeaders() {
		try {
			return remoteURLyBirdDB.getHeaders();
		} catch (RemoteException e) {
			throw new RuntimeException("TODO");
		}
	}

	@Override
	public int create(String[] data) throws DuplicateKeyException {
		try {
			return remoteURLyBirdDB.create(data);
		} catch (RemoteException e) {
			throw new RuntimeException("TODO");
		}
	}

	@Override
	public void delete(int recNo, long lockCookie) throws SecurityException, RecordNotFoundException {
		try {
			remoteURLyBirdDB.delete(recNo, lockCookie);
		} catch (RemoteException e) {

		}
	}

	@Override
	public int[] find(String[] criteria) {
		try {
			return remoteURLyBirdDB.find(criteria);
		} catch (RemoteException e) {
			throw new RuntimeException("TODO");
		}
	}

	@Override
	public long lock(int recNo) throws RecordNotFoundException {
		try {
			return remoteURLyBirdDB.lock(recNo);
		} catch (RemoteException e) {
			throw new RuntimeException("TODO");
		}
	}

	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		try {
			return remoteURLyBirdDB.read(recNo);
		} catch (RemoteException e) {
			throw new RuntimeException("TODO");
		}
	}

	@Override
	public void unlock(int recNo, long cookie) throws RecordNotFoundException {
		try {
			remoteURLyBirdDB.unlock(recNo, cookie);
		} catch (RemoteException e) {

		}
	}

	@Override
	public void update(int recNo, String[] data, long lockCookie) throws SecurityException, RecordNotFoundException {
		try {
			remoteURLyBirdDB.update(recNo, data, lockCookie);
		} catch (RemoteException e) {
		}
	}

}
