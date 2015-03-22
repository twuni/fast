package org.twuni.fast;

/**
 * Generates and returns a random address when authentication is requested.
 */
public class AnonymousAuthenticator implements Authenticator {

	@Override
	public byte [] authenticate( byte [] realm, byte [] credential ) {
		return Integer.toHexString( (int) Math.floor( Math.random() * 0x7FFFFFFF ) ).getBytes();
	}

}
