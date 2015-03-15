package org.twuni.fast;

import org.twuni.fast.model.Packet;

public class PacketListenerWrapper extends EventHandlerBase {

	private final PacketListener wrapped;

	public PacketListenerWrapper( PacketListener wrapped ) {
		this.wrapped = wrapped;
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		wrapped.onPacketReceived( packet );
	}

	@Override
	public void onPacketSent( Packet packet ) {
		wrapped.onPacketSent( packet );
	}

}
