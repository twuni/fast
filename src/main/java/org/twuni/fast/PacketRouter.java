package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * Routes a packet to its destination.
 */
public interface PacketRouter extends FAST {

	/**
	 * Routes a packet to its destination.
	 *
	 * @param packet
	 *            the packet to be routed.
	 */
	public void routePacket( Packet packet );

}
