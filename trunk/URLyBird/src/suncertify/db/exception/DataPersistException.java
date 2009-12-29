package suncertify.db.exception;

public class DataPersistException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataPersistException() {
	}

	public DataPersistException(String message) {
		super(message);
	}

	public DataPersistException(String message, Throwable cause) {
		super(message, cause);
	}
}
