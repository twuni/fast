package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * Wraps a {@link ConnectionListener} and dispatches events to it at the
 * appropriate times.
 */
public class ConnectionListenerWrapper extends EventHandlerBase {

	private final ConnectionListener wrapped;
	private final WriteChannel channel;

	/**
	 * Wraps the given connection listener.
	 *
	 * @param wrapped
	 *            the packet listener to wrap.
	 * @param channel
	 *            the channel for which to report connection events.
	 */
	public ConnectionListenerWrapper( ConnectionListener wrapped, WriteChannel channel ) {
		this.wrapped = wrapped;
		this.channel = channel;
	}

	@Override
	public void onDisconnected() {
		if( wrapped != null ) {
			wrapped.onDisconnected( channel );
		}
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		if( wrapped != null ) {
			wrapped.onConnected( channel );
		}
	}

}
