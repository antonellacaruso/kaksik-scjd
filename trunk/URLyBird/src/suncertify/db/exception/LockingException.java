package suncertify.db.exception;

public class LockingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LockingException() {

	}

	public LockingException(String message) {
		super(message);
	}

	public LockingException(String message, Throwable cause) {
		super(message, cause);
	}
}
