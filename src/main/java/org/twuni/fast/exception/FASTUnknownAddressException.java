package org.twuni.fast.exception;

/**
 * This exception indicates that an address specified in a FAST command was
 * unknown or invalid.
 */
public class FASTUnknownAddressException extends FASTReadException {

	private static final long serialVersionUID = 1L;

	public FASTUnknownAddressException() {
		super();
	}

	public FASTUnknownAddressException( String message ) {
		super( message );
	}

	public FASTUnknownAddressException( String message, Throwable cause ) {
		super( message, cause );
	}

	public FASTUnknownAddressException( Throwable cause ) {
		super( cause );
	}

}
