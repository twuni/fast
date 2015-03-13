package org.twuni.fast.exception;

/**
 * This exception is expected to be thrown whenever a remote endpoint has
 * rejected a greeting.
 */
public class GreetingRejectedException extends FASTException {

	public static final long serialVersionUID = 1L;

	public GreetingRejectedException() {
		super();
	}

}
