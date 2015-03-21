package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * Handles delivery of incoming packets.
 */
public class PacketDeliveryHandler extends EventHandlerBase {

	private final PacketRouter packetRouter;

	public PacketDeliveryHandler( PacketRouter packetRouter ) {
		this.packetRouter = packetRouter;
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		packetRouter.routePacket( packet );
	}

}
