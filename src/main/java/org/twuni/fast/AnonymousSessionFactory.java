package org.twuni.fast;

/**
 * Generates and returns a random session ID when session creation is requested.
 */
public class AnonymousSessionFactory implements SessionFactory {

	@Override
	public byte [] createSession( byte [] address ) {
		return Integer.toHexString( (int) Math.floor( Math.random() * 0x7FFFFFFF ) ).getBytes();
	}

}
