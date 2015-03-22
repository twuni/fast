package org.twuni.fast;

import org.twuni.fast.exception.FASTInvalidCredentialException;

/**
 * Authenticates a credential, mapping it to a local address.
 */
public interface Authenticator {

	/**
	 * Authenticates a given {@code credential} and returns the address with
	 * which it is associated.
	 *
	 * @param realm
	 *            the realm for which the given credential should be valid.
	 * @param credential
	 *            the credential for which authentication is being requested.
	 * @return the address for which the given credential is authenticated.
	 * @throws FASTInvalidCredentialException
	 *             if the given {@code credential} cannot be mapped to an
	 *             address managed by this node.
	 */
	public byte [] authenticate( byte [] realm, byte [] credential );

}
