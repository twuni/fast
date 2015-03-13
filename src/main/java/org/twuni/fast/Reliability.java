package org.twuni.fast;

import java.util.ArrayList;
import java.util.List;

import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

/**
 * This helper object can be integrated with an {@link EventHandler} and a
 * {@link WriteChannel} to manage packet delivery reliability within a session.
 */
public class Reliability {

	private static final Packet [] OF_PACKETS = new Packet [0];

	private int receivedPacketCount;
	private int sentPacketCount;
	private final List<Packet> unacknowledgedPackets = new ArrayList<Packet>();
	private final WriteChannel channel;

	public Reliability( Session session ) {
		this( session.write() );
	}

	public Reliability( WriteChannel channel ) {
		this.channel = channel;
	}

	public void reset() {
		receivedPacketCount = 0;
		sentPacketCount = 0;
		unacknowledgedPackets.clear();
	}

	public int onPacketSent( Packet packet ) {
		try {
			unacknowledgedPackets.add( packet );
			sentPacketCount++;
		} catch( Throwable exception ) {
			onException( exception );
		}
		return sentPacketCount;
	}

	public int onPacketReceived() {
		receivedPacketCount++;
		return receivedPacketCount;
	}

	public void onAcknowledgmentReceived( int n ) {
		if( n == sentPacketCount ) {
			unacknowledgedPackets.clear();
			return;
		}
		flush();
	}

	public void onAcknowledgmentRequested() {
		try {
			channel.sendAcknowledgment( receivedPacketCount );
		} catch( Throwable exception ) {
			onException( exception );
		}
	}

	public void flush() {
		sentPacketCount -= unacknowledgedPackets.size();
		try {
			channel.send( unacknowledgedPackets.toArray( OF_PACKETS ) );
			channel.requestAcknowledgment();
		} catch( Throwable exception ) {
			onException( exception );
		}
	}

	protected void onException( Throwable exception ) {
		exception.printStackTrace();
	}

	public List<Packet> getUnacknowledgedPackets() {
		return unacknowledgedPackets;
	}

	public int getReceivedPacketCount() {
		return receivedPacketCount;
	}

	public int getSentPacketCount() {
		return sentPacketCount;
	}

}
