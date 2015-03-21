package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * Creates a local session when an ATTACH command is received, then sends the
 * session ID to the remote endpoint.
 */
public class ServerSessionCreator extends EventHandlerBase {

	private final WriteChannel channel;
	private final SessionFactory sessionFactory;

	/**
	 * Initializes this session creation handler to write to the given
	 * {@code channel} and to use the given {@code sessionFactory} to create
	 * sessions.
	 *
	 * @param channel
	 *            the channel to which the session ID will be sent after
	 *            creation.
	 * @param sessionFactory
	 *            the creator of sessions.
	 */
	public ServerSessionCreator( WriteChannel channel, SessionFactory sessionFactory ) {
		this.channel = channel;
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		byte [] sessionID = sessionFactory.createSession( address );
		channel.session( sessionID );
	}

}
