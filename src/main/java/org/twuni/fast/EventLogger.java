package org.twuni.fast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.twuni.fast.model.Packet;

/**
 * This event handler simply logs events to the wrapped print stream.
 */
public class EventLogger extends EventHandlerBase {

	private final PrintStream logger;

	public EventLogger( OutputStream output ) {
		this( output != null ? new PrintStream( output ) : null );
	}

	public EventLogger( PrintStream logger ) {
		this.logger = logger;
	}

	protected void log( String format, Object... args ) {
		if( logger != null ) {
			logger.println( String.format( format, args ) );
		}
	}

	@Override
	public void onAcknowledgmentReceived( int n ) {
		log( "#onAcknowledgmentReceived(%d)", Integer.valueOf( n ) );
	}

	@Override
	public void onAcknowledgmentRequested() {
		log( "#onAcknowledgmentRequested" );
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		log( "#onAttachRequested(%s)", Arrays.toString( address ) );
	}

	@Override
	public void onConnected() {
		log( "#onConnected" );
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		log( "#onCredentialReceived(%s)", Arrays.toString( credential ) );
	}

	@Override
	public void onDisconnected() {
		log( "#onDisconnected" );
	}

	@Override
	public void onException( Throwable exception ) {
		log( "#onException(%s)", exception.getClass().getName() );
	}

	@Override
	public void onFetchRequested() {
		log( "#onFetchRequested" );
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		log( "#onIdentityReceived(%s)", Arrays.toString( identity ) );
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		log( "#onPacketReceived(%s)", packet );
	}

	@Override
	public void onPacketSent( Packet packet ) {
		log( "#onPacketSent(%s)", packet );
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		log( "#onSessionCreated(%s)", Arrays.toString( sessionID ) );
	}

}
