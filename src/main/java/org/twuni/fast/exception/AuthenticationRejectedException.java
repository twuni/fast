package org.twuni.fast.exception;

/**
 * This exception is intended to be thrown whenever a remote endpoint has
 * rejected an authentication request.
 */
public class AuthenticationRejectedException extends FASTException {

	public static final long serialVersionUID = 1L;

	public AuthenticationRejectedException() {
		super();
	}

}
