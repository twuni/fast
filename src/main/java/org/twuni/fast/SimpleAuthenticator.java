package org.twuni.fast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.twuni.fast.exception.FASTInvalidCredentialException;

/**
 * This authenticator consults an internal mapping of credentials to identities.
 */
public class SimpleAuthenticator implements Authenticator {

	private static String toLocator( byte [] array ) {
		return Integer.toHexString( Arrays.hashCode( array ) );
	}

	private final Map<String, byte []> credentials = new HashMap<String, byte []>();

	@Override
	public byte [] authenticate( byte [] credential ) {
		byte [] identity = credentials.get( toLocator( credential ) );
		if( identity == null ) {
			throw new FASTInvalidCredentialException();
		}
		return identity;
	}

	/**
	 * Accept the given {@code credential} for the given {@code identity}.
	 *
	 * @param identity
	 *            the identity for which the given {@code credential} is valid.
	 * @param credential
	 *            the credential to accept for the given {@code identity}.
	 */
	public void acceptCredential( byte [] identity, byte [] credential ) {
		credentials.put( toLocator( credential ), identity );
	}

	/**
	 * Configure this authenticator to reject the given {@code credential}.
	 *
	 * @param credential
	 *            the credential to be rejected.
	 */
	public void rejectCredential( byte [] credential ) {
		credentials.remove( toLocator( credential ) );
	}

}
