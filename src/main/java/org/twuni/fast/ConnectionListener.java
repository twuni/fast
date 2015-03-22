package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * This listener is notified of channel connection events.
 */
public interface ConnectionListener {

	/**
	 * This method is notified whenever a write channel has connected.
	 *
	 * @param writeChannel
	 *            the channel that has just connected.
	 */
	public void onConnected( WriteChannel writeChannel );

	/**
	 * This method is notified whenever a write channel has disconnected.
	 *
	 * @param writeChannel
	 *            the channel that has just disconnected.
	 */
	public void onDisconnected( WriteChannel writeChannel );

}
