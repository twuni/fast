package org.twuni.fast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.twuni.fast.io.PacketSerializer;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.IOUtils;

/**
 * This helper object can be integrated with an {@link EventHandler} and a
 * {@link WriteChannel} to manage packet delivery reliability within a session.
 */
public class Reliability implements FAST {

	private static final int SERIAL_VERSION = 1;

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

	public void flush() {
		int unacknowledgedPacketsCount = unacknowledgedPackets.size();
		Packet [] unacknowledgedPacketsArray = new Packet [unacknowledgedPacketsCount];
		unacknowledgedPackets.toArray( unacknowledgedPacketsArray );
		unacknowledgedPackets.clear();
		sentPacketCount -= unacknowledgedPacketsCount;
		try {
			channel.send( unacknowledgedPacketsArray );
			channel.requestAcknowledgment();
		} catch( Throwable exception ) {
			onException( exception );
		}
	}

	public int getReceivedPacketCount() {
		return receivedPacketCount;
	}

	public int getSentPacketCount() {
		return sentPacketCount;
	}

	public List<Packet> getUnacknowledgedPackets() {
		return unacknowledgedPackets;
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

	protected void onException( Throwable exception ) {
		exception.printStackTrace();
	}

	public int onPacketReceived() {
		receivedPacketCount++;
		return receivedPacketCount;
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

	public void reset() {
		receivedPacketCount = 0;
		sentPacketCount = 0;
		unacknowledgedPackets.clear();
	}

	public void restoreState( InputStream input ) throws IOException {
		int version = IOUtils.readInt( input );
		switch( version ) {
			case 1:
				receivedPacketCount = IOUtils.readInt( input );
				sentPacketCount = IOUtils.readInt( input );
				int unacknowledgedPacketsCount = IOUtils.readInt( input );
				unacknowledgedPackets.clear();
				for( int i = 0; i < unacknowledgedPacketsCount; i++ ) {
					unacknowledgedPackets.add( PacketSerializer.read( input ) );
				}
				break;
			default:
		}
	}

	public void saveState( OutputStream output ) throws IOException {
		IOUtils.writeInt( output, SERIAL_VERSION );
		IOUtils.writeInt( output, receivedPacketCount );
		IOUtils.writeInt( output, sentPacketCount );
		IOUtils.writeInt( output, unacknowledgedPackets.size() );
		for( Packet packet : unacknowledgedPackets ) {
			PacketSerializer.write( packet, output );
		}
	}

}
