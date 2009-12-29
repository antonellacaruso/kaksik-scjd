package suncertify.db.exception;

public class RecordNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public RecordNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public RecordNotFoundException(String message) {
		super(message);
	}

	public RecordNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
