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
 * This helper object manages packet delivery reliability within a session.
 */
public class Reliability extends EventHandlerBase implements Stateful {

	private static final int SERIAL_VERSION = 1;

	private int receivedPacketCount;
	private int sentPacketCount;
	private final List<Packet> unacknowledgedPackets = new ArrayList<Packet>();
	private final WriteChannel channel;

	/**
	 * Convenience constructor for {@link #Reliability(WriteChannel)} using the
	 * given {@code session}'s write channel.
	 *
	 * @param session
	 *            the session whose write channel will be used to send commands
	 *            when necessary.
	 * @see #Reliability(WriteChannel)
	 */
	public Reliability( Session session ) {
		this( session.write() );
	}

	/**
	 * Initializes a new Reliability handler which writes to the given
	 * {@code channel}.
	 *
	 * @param channel
	 *            the channel to which this handler will write commands when
	 *            necessary.
	 */
	public Reliability( WriteChannel channel ) {
		this.channel = channel;
	}

	private synchronized void flush() {
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

	@Override
	public void onAcknowledgmentReceived( int n ) {
		if( n == sentPacketCount ) {
			unacknowledgedPackets.clear();
			return;
		}
		flush();
	}

	@Override
	public void onAcknowledgmentRequested() {
		try {
			channel.sendAcknowledgment( receivedPacketCount );
		} catch( Throwable exception ) {
			onException( exception );
		}
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		receivedPacketCount++;
	}

	@Override
	public void onPacketSent( Packet packet ) {
		try {
			unacknowledgedPackets.add( packet );
			sentPacketCount++;
		} catch( Throwable exception ) {
			onException( exception );
		}
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		reset();
	}

	private synchronized void reset() {
		receivedPacketCount = 0;
		sentPacketCount = 0;
		unacknowledgedPackets.clear();
	}

	@Override
	public synchronized void restoreState( InputStream input ) throws IOException {
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

	@Override
	public synchronized void saveState( OutputStream output ) throws IOException {
		IOUtils.writeInt( output, SERIAL_VERSION );
		IOUtils.writeInt( output, receivedPacketCount );
		IOUtils.writeInt( output, sentPacketCount );
		IOUtils.writeInt( output, unacknowledgedPackets.size() );
		for( Packet packet : unacknowledgedPackets ) {
			PacketSerializer.write( packet, output );
		}
	}

}
