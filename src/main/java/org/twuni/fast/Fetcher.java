package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * Sends a FETCH command after establishing an identity with the remote
 * endpoint.
 */
public class Fetcher extends EventHandlerBase {

	private final WriteChannel channel;

	/**
	 * Initializes this fetcher to write to the given {@code session}'s write
	 * channel.
	 *
	 * @param session
	 *            the session whose write channel will receive a FETCH command.
	 * @see #Fetcher(WriteChannel)
	 */
	public Fetcher( Session session ) {
		this( session.write() );
	}

	/**
	 * Initializes this fetcher to write to the given {@code channel}.
	 *
	 * @param channel
	 *            the channel to which the FETCH command will be written.
	 */
	public Fetcher( WriteChannel channel ) {
		this.channel = channel;
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		channel.fetch();
	}

}
