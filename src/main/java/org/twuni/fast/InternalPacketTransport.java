package org.twuni.fast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.twuni.fast.exception.FASTWriteException;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

/**
 * Provides and delivers packets to local addresses.
 */
public class InternalPacketTransport implements MailboxFactory, PacketRouter {

	private static String toLocator( byte [] address ) {
		return Integer.toHexString( Arrays.hashCode( address ) );
	}

	private final Map<String, List<Packet>> registry = new HashMap<String, List<Packet>>();
	private final WriteChannelProvider writeChannelProvider;

	public InternalPacketTransport( WriteChannelProvider writeChannelProvider ) {
		this.writeChannelProvider = writeChannelProvider;
	}

	@Override
	public Mailbox createMailbox( byte [] address ) {
		return new SimpleMailbox( getPacketList( address ) );
	}

	private List<Packet> getPacketList( byte [] address ) {
		String locator = toLocator( address );
		List<Packet> packetList = registry.get( locator );
		if( packetList == null ) {
			packetList = new ArrayList<Packet>();
			registry.put( locator, packetList );
		}
		return packetList;
	}

	@Override
	public void routePacket( Packet packet ) {

		byte [] address = packet.getTo();

		if( Arrays.equals( packet.getFrom(), address ) ) {
			// Treat an echo packet as if it has already been sent.
			return;
		}

		Set<WriteChannel> channels = writeChannelProvider.provideWriteChannels( address );

		if( channels.isEmpty() ) {
			getPacketList( address ).add( packet );
			return;
		}

		for( WriteChannel channel : channels ) {
			try {
				channel.send( packet );
			} catch( FASTWriteException exception ) {
				writeChannelProvider.detach( address, channel );
			}
		}

	}

}
