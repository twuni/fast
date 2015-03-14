package org.twuni.fast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

/**
 * This event handler implements reliable packet delivery.
 */
public class ReliableEventHandler extends EventHandlerBase {

	private final Reliability reliability;

	public ReliableEventHandler( Reliability reliability ) {
		this.reliability = reliability;
	}

	public ReliableEventHandler( WriteChannel writeChannel ) {
		this( new Reliability( writeChannel ) );
	}

	public ReliableEventHandler( Session session ) {
		this( session.write() );
	}

	@Override
	public void onAcknowledgmentReceived( int n ) {
		reliability.onAcknowledgmentReceived( n );
	}

	@Override
	public void onAcknowledgmentRequested() {
		reliability.onAcknowledgmentRequested();
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		reliability.reset();
	}

	@Override
	public void onFetchRequested() {
		reliability.flush();
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		reliability.onPacketReceived();
	}

	@Override
	public void onPacketSent( Packet packet ) {
		reliability.onPacketSent( packet );
	}

	public void restoreState( InputStream input ) throws IOException {
		reliability.restoreState( input );
	}

	public void saveState( OutputStream output ) throws IOException {
		reliability.saveState( output );
	}

}
