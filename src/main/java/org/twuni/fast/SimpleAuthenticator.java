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

	private final Map<String, Map<String, byte []>> realms = new HashMap<String, Map<String, byte []>>();

	/**
	 * Accept the given {@code credential} for the given {@code identity}.
	 *
	 * @param realm
	 *            the realm in which to accept the given {@code credential}.
	 * @param identity
	 *            the identity for which the given {@code credential} is valid.
	 * @param credential
	 *            the credential to accept for the given {@code identity}.
	 */
	public void acceptCredential( byte [] realm, byte [] identity, byte [] credential ) {
		getCredentials( realm ).put( toLocator( credential ), identity );
	}

	@Override
	public byte [] authenticate( byte [] realm, byte [] credential ) {
		byte [] identity = getIdentity( realm, credential );
		if( identity == null ) {
			throw new FASTInvalidCredentialException();
		}
		return identity;
	}

	private Map<String, byte []> getCredentials( byte [] realm ) {
		String realmLocator = toLocator( realm );
		Map<String, byte []> credentials = realms.get( realmLocator );
		if( credentials == null ) {
			credentials = new HashMap<String, byte []>();
			realms.put( realmLocator, credentials );
		}
		return credentials;
	}

	private byte [] getIdentity( byte [] realm, byte [] credential ) {
		return getCredentials( realm ).get( toLocator( credential ) );
	}

	/**
	 * Tests whether the given {@code identity} has an acceptable credential.
	 *
	 * @param realm
	 *            the realm in which to check.
	 * @param identity
	 *            the identity to test for the existence of a valid credential.
	 * @return {@code true} if the given {@code identity} has an acceptable
	 *         credential. Otherwise returns {@code false}.
	 */
	protected boolean hasAcceptableCredential( byte [] realm, byte [] identity ) {
		for( byte [] value : getCredentials( realm ).values() ) {
			if( Arrays.equals( identity, value ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Configure this authenticator to reject the given {@code credential}.
	 *
	 * @param realm
	 *            the realm in which the given credential should be rejected.
	 * @param credential
	 *            the credential to be rejected.
	 */
	public void rejectCredential( byte [] realm, byte [] credential ) {
		getCredentials( realm ).remove( toLocator( credential ) );
	}

}
