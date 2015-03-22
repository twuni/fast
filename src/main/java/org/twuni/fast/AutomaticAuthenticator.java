package org.twuni.fast;

/**
 * Automatically accepts a credential given for an identity that has never
 * authenticated before.
 */
public class AutomaticAuthenticator extends SimpleAuthenticator {

	private static byte [] parseIdentity( byte [] realm, byte [] credential ) {

		int n = -1;

		for( int i = 0; i < credential.length; i++ ) {
			if( credential[i] == '\n' ) {
				n = i;
				break;
			}
		}

		byte [] identity = new byte [n + realm.length + 1];
		System.arraycopy( credential, 0, identity, 0, n );
		identity[n] = '@';
		System.arraycopy( realm, 0, identity, n + 1, realm.length );

		return identity;

	}

	@Override
	public byte [] authenticate( byte [] realm, byte [] credential ) {
		byte [] identity = parseIdentity( realm, credential );
		if( !hasAcceptableCredential( realm, identity ) ) {
			acceptCredential( realm, identity, credential );
		}
		return super.authenticate( realm, credential );
	}

}
