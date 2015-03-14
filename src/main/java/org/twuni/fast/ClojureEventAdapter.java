package org.twuni.fast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.twuni.fast.model.Packet;

/**
 * This helper class prints session events to a wrapped output stream in
 * Clojure syntax.
 */
public class ClojureEventAdapter extends EventHandlerBase {

	private final PrintStream output;

	public ClojureEventAdapter( OutputStream output ) {
		this( new PrintStream( output ) );
	}

	public ClojureEventAdapter( PrintStream output ) {
		this.output = output;
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		output.print( String.format( "(rx :timestamp %d :address %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getAddress() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) ) );
	}

	@Override
	public void onPacketSent( Packet packet ) {
		output.print( String.format( "(tx :timestamp %d :address %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getAddress() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) ) );
	}

	@Override
	public void onConnected() {
		output.print( "(connect)" );
	}

	@Override
	public void onDisconnected() {
		output.print( "(disconnect)" );
	}

	@Override
	public void onAcknowledgmentReceived( int n ) {
		output.print( String.format( "(ack %d)", Integer.valueOf( n ) ) );
	}

	@Override
	public void onAcknowledgmentRequested() {
		output.print( "(request-ack)" );
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		output.print( String.format( "(authenticate %s)", Arrays.toString( credential ).replaceAll( ",", "" ) ) );
	}

	@Override
	public void onFetchRequested() {
		output.print( "(fetch)" );
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		output.print( String.format( "(greet %s)", Arrays.toString( address ).replaceAll( ",", "" ) ) );
	}

	@Override
	public void onException( Throwable exception ) {
		output.print( String.format( "(error :type %s :message %s)", exception.getClass().getName(), exception.getLocalizedMessage() ) );
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		output.print( String.format( "(identity %s)", Arrays.toString( identity ).replaceAll( ",", "" ) ) );
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		output.print( String.format( "(session :id %s)", Arrays.toString( sessionID ).replaceAll( ",", "" ) ) );
	}

}
