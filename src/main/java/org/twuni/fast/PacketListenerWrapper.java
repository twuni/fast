package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * Wraps a {@link PacketListener} which is not necessarily an instance of an
 * {@link EventHandler}.
 */
public class PacketListenerWrapper extends EventHandlerBase {

	private final PacketListener wrapped;

	/**
	 * Wraps the given packet listener.
	 *
	 * @param wrapped
	 *            the packet listener to wrap.
	 */
	public PacketListenerWrapper( PacketListener wrapped ) {
		this.wrapped = wrapped;
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		if( wrapped != null ) {
			wrapped.onPacketReceived( packet );
		}
	}

	@Override
	public void onPacketSent( Packet packet ) {
		if( wrapped != null ) {
			wrapped.onPacketSent( packet );
		}
	}

}
