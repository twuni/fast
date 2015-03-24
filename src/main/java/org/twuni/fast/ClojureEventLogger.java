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

	private static String toString( byte [] array ) {
		return array != null ? Arrays.toString( array ).replaceAll( ",", "" ) : "null";
	}

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
		log( "(attach %s)", toString( address ) );
	}

	@Override
	public void onConnected() {
		log( "(connect)" );
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		log( "(authenticate %s)", toString( credential ) );
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
		log( "(identity %s)", toString( identity ) );
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		log( "(rx :timestamp %d :from %s :to %s :payload %d)", Long.valueOf( packet.getTimestamp() ), toString( packet.getFrom() ), toString( packet.getTo() ), Integer.valueOf( packet.getPayload().getLimit() ) );
	}

	@Override
	public void onPacketSent( Packet packet ) {
		log( "(tx :timestamp %d :from %s :to %s :payload %d)", Long.valueOf( packet.getTimestamp() ), toString( packet.getFrom() ), toString( packet.getTo() ), Integer.valueOf( packet.getPayload().getLimit() ) );
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		log( "(session :id %s)", toString( sessionID ) );
	}

}
