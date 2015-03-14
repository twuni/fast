package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * This object simplifies {@link EventHandler} implementations which only need
 * to override a few methods by providing an empty implementation of each
 * method.
 */
public class EventHandlerBase implements EventHandler {

	@Override
	public void onAcknowledgmentReceived( int n ) {
		// By default, do nothing.
	}

	@Override
	public void onAcknowledgmentRequested() {
		// By default, do nothing.
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		// By default, do nothing.
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		// By default, do nothing.
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		// By default, do nothing.
	}

	@Override
	public void onConnected() {
		// By default, do nothing.
	}

	@Override
	public void onDisconnected() {
		// By default, do nothing.
	}

	@Override
	public void onException( Throwable exception ) {
		// By default, do nothing.
	}

	@Override
	public void onFetchRequested() {
		// By default, do nothing.
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		// By default, do nothing.
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		// By default, do nothing.
	}

	@Override
	public void onPacketSent( Packet packet ) {
		// By default, do nothing.
	}

}
