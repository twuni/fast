package org.twuni.fast.exception;

public class FASTWriteException extends FASTException {

	private static final long serialVersionUID = 1L;

	public FASTWriteException() {
		// Default constructor.
	}

	public FASTWriteException( String message ) {
		super( message );
	}

	public FASTWriteException( String message, Throwable cause ) {
		super( message, cause );
	}

	public FASTWriteException( Throwable cause ) {
		super( cause );
	}

}
