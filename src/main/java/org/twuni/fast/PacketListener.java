package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * A packet listener is notified whenever a packet is sent or received.
 */
public interface PacketListener extends FAST {

	/**
	 * This method is called just after a packet is received.
	 *
	 * @param packet
	 *            the received packet.
	 */
	public void onPacketReceived( Packet packet );

	/**
	 * This method is called just after a packet has been sent.
	 *
	 * @param packet
	 *            the sent packet.
	 */
	public void onPacketSent( Packet packet );

}
