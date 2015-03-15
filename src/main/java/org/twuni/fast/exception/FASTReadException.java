package org.twuni.fast.exception;

public class FASTReadException extends FASTException {

	private static final long serialVersionUID = 1L;

	public FASTReadException() {
		// Default constructor.
	}

	public FASTReadException( String message ) {
		super( message );
	}

	public FASTReadException( String message, Throwable cause ) {
		super( message, cause );
	}

	public FASTReadException( Throwable cause ) {
		super( cause );
	}

}
