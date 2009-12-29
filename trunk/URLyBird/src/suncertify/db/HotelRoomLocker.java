package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import suncertify.db.exception.LockingException;
import suncertify.db.exception.RecordNotFoundException;
import suncertify.util.RandomLongGenerator;

public class HotelRoomLocker {

	private static final int LOCK_TIMEOUT = 10 * 1000; // 10 seconds.

	private static Lock lock = new ReentrantLock();

	private static Condition lockReleased = lock.newCondition();

	private static Map<Integer, Long> recordLocks = new HashMap<Integer, Long>();

	public static long lockHotelRoom(int recNo) {
		lock.lock();

		try {
			long endTimeInMillis = System.currentTimeMillis() + LOCK_TIMEOUT;
			// If recordLocks contains current record number, then this record
			// is locked. Therefore waiting until recurd gets available or
			// timeout occurs.
			while (recordLocks.containsKey(recNo)) {
				long availableMilliseconds = endTimeInMillis
						- System.currentTimeMillis();

				// Awaiting inside while loop in case of a spurious wakeup.
				try {
					if (!lockReleased.await(availableMilliseconds,
							TimeUnit.MILLISECONDS)) {
						// Could not lock given record.
						throw new LockingException(
								"Couldn't get lock on given record. Timeout occured.");
					}
				} catch (InterruptedException e) {
					throw new LockingException(
							"Thread interuption exception occured", e);
				}
			}

			long randomCookie = RandomLongGenerator.generate();

			recordLocks.put(recNo, randomCookie);

			return randomCookie;
		} finally {
			lock.unlock();
		}
	}

	public static void unlockHotelRoom(int recNo, long cookie)
			throws RecordNotFoundException {
		lock.lock();

		if (!recordLocks.containsKey(recNo)) {
			throw new RecordNotFoundException("Record is not locked");
		}

		long lockingCookie = recordLocks.get(recNo);
		if (lockingCookie == cookie) {
			recordLocks.remove(recNo);
			lockReleased.signal();
		} else {
			throw new SecurityException(
					"Cannot unlock hotel room. Wrong cookie was specified.");
		}

		lock.unlock();
	}

	public static void ensureLock(int recNo, long cookie)
			throws RecordNotFoundException {
		lock.lock();

		try {
			if (!recordLocks.containsKey(recNo)) {
				throw new RecordNotFoundException("Record is not locked.");
			}

			long lockingCookie = recordLocks.get(recNo);
			if (lockingCookie != cookie) {
				throw new SecurityException(
						"Record lock is not held by given cookie.");
			}
		} finally {
			lock.unlock();
		}

	}
}
