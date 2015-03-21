package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

/**
 * When a FETCH command is received, attempts to flush all queued packets from a
 * wrapped packet provider.
 */
public class FlushPacketsOnFetch extends EventHandlerBase {

	private final WriteChannel channel;
	private final PacketProviderFactory packetProviderFactory;

	/**
	 * Initializes this handler to send packets along the given {@code channel},
	 * using the given {@code packetProvider} to find packets to send.
	 *
	 * @param channel
	 *            the channel along which to send fetched packets.
	 * @param packetProviderFactory
	 *            the factory responsible for creating a provider of packets to
	 *            send.
	 */
	public FlushPacketsOnFetch( WriteChannel channel, PacketProviderFactory packetProviderFactory ) {
		this.channel = channel;
		this.packetProviderFactory = packetProviderFactory;
	}

	@Override
	public void onFetchRequested() {
		PacketProvider packetProvider = packetProviderFactory.createPacketProvider( channel.getRemoteAddress() );
		for( Packet packet = packetProvider.providePacket(); packet != null; packet = packetProvider.providePacket() ) {
			channel.send( packet );
		}
		channel.requestAcknowledgment();
	}

}
