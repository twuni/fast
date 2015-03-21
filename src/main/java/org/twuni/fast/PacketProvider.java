package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * Provides a packet, if available.
 */
public interface PacketProvider {

	/**
	 * Returns a packet, if available.
	 *
	 * @return a packet, if available. Otherwise, returns {@code null}.
	 */
	public Packet providePacket();

}
