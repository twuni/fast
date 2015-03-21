package org.twuni.fast.exception;

/**
 * This exception indicates that something has gone wrong while attempting to
 * read a FAST command.
 */
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
