package org.twuni.fast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.twuni.fast.model.Packet;

/**
 * This helper class prints session events to a wrapped output stream in
 * Clojure syntax.
 */
public class ClojureEventLogger extends EventLogger {

	public ClojureEventLogger( OutputStream output ) {
		super( output );
	}

	public ClojureEventLogger( PrintStream output ) {
		super( output );
	}

	@Override
	public void onAcknowledgmentReceived( int n ) {
		log( "(ack %d)", Integer.valueOf( n ) );
	}

	@Override
	public void onAcknowledgmentRequested() {
		log( "(request-ack)" );
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		log( "(attach %s)", Arrays.toString( address ).replaceAll( ",", "" ) );
	}

	@Override
	public void onConnected() {
		log( "(connect)" );
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		log( "(authenticate %s)", Arrays.toString( credential ).replaceAll( ",", "" ) );
	}

	@Override
	public void onDisconnected() {
		log( "(disconnect)" );
	}

	@Override
	public void onException( Throwable exception ) {
		if( exception.getLocalizedMessage() != null ) {
			log( "(error :type \"%s\" :message \"%s\")", exception.getClass().getName(), exception.getLocalizedMessage() );
		} else {
			log( "(error :type \"%s\")", exception.getClass().getName() );
		}
	}

	@Override
	public void onFetchRequested() {
		log( "(fetch)" );
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		log( "(identity %s)", Arrays.toString( identity ).replaceAll( ",", "" ) );
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		log( "(rx :timestamp %d :from %s to: %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getFrom() ).replaceAll( ",", "" ), Arrays.toString( packet.getTo() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) );
	}

	@Override
	public void onPacketSent( Packet packet ) {
		log( "(tx :timestamp %d from: %s :to %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getFrom() ).replaceAll( ",", "" ), Arrays.toString( packet.getTo() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) );
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		log( "(session :id %s)", Arrays.toString( sessionID ).replaceAll( ",", "" ) );
	}

}
