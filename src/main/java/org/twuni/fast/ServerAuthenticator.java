package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * Handles authentication for a FAST session.
 */
public class ServerAuthenticator extends EventHandlerBase {

	private final WriteChannel channel;
	private final Authenticator authenticator;

	/**
	 * Initializes this handler to reply along the given {@code channel} after
	 * verifying a credential with the given {@code authenticator}.
	 *
	 * @param channel
	 *            the channel to which this handler will reply after
	 *            authentication.
	 * @param authenticator
	 *            the authenticator responsible for verifying credentials.
	 */
	public ServerAuthenticator( WriteChannel channel, Authenticator authenticator ) {
		this.channel = channel;
		this.authenticator = authenticator;
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		super.onAttachRequested( address );
		channel.setLocalAddress( address );
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		byte [] address = authenticator.authenticate( channel.getLocalAddress(), credential );
		channel.identify( address );
	}

}
