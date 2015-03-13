package org.twuni.fast;

import java.util.Arrays;

import org.twuni.fast.model.Packet;

/**
 * This helper class prints session events to the system console in
 * human-readable form.
 */
public class ClojureEventAdapter implements EventHandler {

	@Override
	public void onPacketReceived( Packet packet ) {
		System.out.print( String.format( "(rx :timestamp %d :address %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getAddress() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) ) );
	}

	@Override
	public void onPacketSent( Packet packet ) {
		System.out.print( String.format( "(tx :timestamp %d :address %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getAddress() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) ) );
	}

	@Override
	public void onConnected() {
		System.out.print( "(connect)" );
	}

	@Override
	public void onDisconnected() {
		System.out.print( "(disconnect)" );
	}

	@Override
	public void onAcknowledgmentReceived( int n ) {
		System.out.print( String.format( "(ack %d)", Integer.valueOf( n ) ) );
	}

	@Override
	public void onAcknowledgmentRequested() {
		System.out.print( "(request-ack)" );
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		System.out.print( String.format( "(authenticate %s)", Arrays.toString( credential ).replaceAll( ",", "" ) ) );
	}

	@Override
	public void onFetchRequested() {
		System.out.print( "(fetch)" );
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		System.out.print( String.format( "(greet %s)", Arrays.toString( address ).replaceAll( ",", "" ) ) );
	}

	@Override
	public void onException( Throwable exception ) {
		System.out.print( String.format( "(error :type %s :message %s)", exception.getClass().getName(), exception.getLocalizedMessage() ) );
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		System.out.print( String.format( "(identity %s)", Arrays.toString( identity ).replaceAll( ",", "" ) ) );
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		System.out.print( String.format( "(session :id %s)", Arrays.toString( sessionID ).replaceAll( ",", "" ) ) );
	}

}
