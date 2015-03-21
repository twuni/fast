package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * Provides a credential at the appropriate time when establishing a session.
 */
public class CredentialProvider extends EventHandlerBase {

	private final WriteChannel channel;
	private final byte [] credential;

	/**
	 * Initializes a new credential provider which provides its credential via
	 * the given {@code session}'s write channel.
	 *
	 * @param credential
	 *            the credential to be provided.
	 * @param session
	 *            the session whose write channel will receive the given
	 *            {@code credential}.
	 * @see #CredentialProvider(byte[], WriteChannel)
	 */
	public CredentialProvider( byte [] credential, Session session ) {
		this( credential, session.write() );
	}

	/**
	 * Initializes a new credential provider which provides its credential via
	 * the given {@code channel}.
	 *
	 * @param credential
	 *            the credential to be provided.
	 * @param channel
	 *            the channel to which the credential will be written.
	 */
	public CredentialProvider( byte [] credential, WriteChannel channel ) {
		this.credential = credential;
		this.channel = channel;
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		channel.authenticate( credential );
	}

}
