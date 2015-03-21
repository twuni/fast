package org.twuni.fast;

import org.twuni.fast.exception.FASTException;
import org.twuni.fast.io.WriteChannel;

/**
 * Detaches a session whenever an unhandled exception occurs.
 */
public class DetachOnException extends EventHandlerBase {

	private final WriteChannel channel;

	/**
	 * Initializes this handler to detach from the given {@code channel}
	 * whenever an exception occurs.
	 *
	 * @param channel
	 *            the channel to be detached whenever an exception occurs.
	 */
	public DetachOnException( WriteChannel channel ) {
		this.channel = channel;
	}

	@Override
	public void onException( Throwable exception ) {
		try {
			channel.detach();
		} catch( FASTException ignore ) {
			// Ignore, to prevent infinite recursion.
		}
	}

}
