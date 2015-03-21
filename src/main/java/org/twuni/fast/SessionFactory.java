package org.twuni.fast;

/**
 * Creates a session for a responder node.
 */
public interface SessionFactory {

	/**
	 * Create a new session attached to the given {@code address} and returns an
	 * identifier for that session.
	 *
	 * @param address
	 *            the address to which the new session should be attached.
	 * @return the identifier for the session created by this method.
	 */
	public byte [] createSession( byte [] address );

}
