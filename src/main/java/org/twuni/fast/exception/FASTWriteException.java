package org.twuni.fast.exception;

/**
 * This exception indicates that something has gone wrong while writing a FAST
 * command.
 */
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
