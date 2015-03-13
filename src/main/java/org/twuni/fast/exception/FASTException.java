package org.twuni.fast.exception;

import org.twuni.fast.FAST;

/**
 * This exception is when something generally goes wrong in FAST.
 */
public class FASTException extends Exception implements FAST {

	private static final long serialVersionUID = 1L;

	public FASTException() {
		// Default constructor
	}

	public FASTException( String message ) {
		super( message );
	}

	public FASTException( Throwable cause ) {
		super( cause );
	}

	public FASTException( String message, Throwable cause ) {
		super( message, cause );
	}

}
