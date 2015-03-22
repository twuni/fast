package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * This event handler implements the default behavior for a {@link Client}.
 */
public class ClientEventHandler extends EventHandlers {

	private WriteChannel channel;

	/**
	 * Initializes this event handler to use the given {@code credential} for
	 * authentication and notifying the given {@code packetListener} of
	 * incoming/outgoing packets.
	 *
	 * @param channel
	 *            the channel to which commands will be written.
	 * @param credential
	 *            the credential to provide to the remote endpoint when
	 *            authentication is necessary.
	 * @param packetListener
	 *            the listener to be notified of incoming and outgoing packets.
	 */
	public ClientEventHandler( WriteChannel channel, byte [] credential, PacketListener packetListener ) {
		super( new DetachOnException( channel ), new Reliability( channel ), new PacketListenerWrapper( packetListener ), new CredentialProvider( credential, channel ), new Fetcher( channel ) );
		this.channel = channel;
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		super.onIdentityReceived( identity );
		channel.setLocalAddress( identity );
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		super.onSessionCreated( sessionID );
		channel.setSessionID( sessionID );
	}

}
