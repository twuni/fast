package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * This event handler simply dispatches its events to each wrapped event
 * handler, in sequence.
 */
public class EventHandlers extends EventHandlerBase {

	private final EventHandler [] eventHandlers;

	public EventHandlers( EventHandler... eventHandlers ) {
		this.eventHandlers = eventHandlers;
	}

	@Override
	public void onAcknowledgmentReceived( int n ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onAcknowledgmentReceived( n );
		}
	}

	@Override
	public void onAcknowledgmentRequested() {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onAcknowledgmentRequested();
		}
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onAttachRequested( address );
		}
	}

	@Override
	public void onConnected() {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onConnected();
		}
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onCredentialReceived( credential );
		}
	}

	@Override
	public void onDisconnected() {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onDisconnected();
		}
	}

	@Override
	public void onException( Throwable exception ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onException( exception );
		}
	}

	@Override
	public void onFetchRequested() {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onFetchRequested();
		}
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onIdentityReceived( identity );
		}
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onPacketReceived( packet );
		}
	}

	@Override
	public void onPacketSent( Packet packet ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onPacketSent( packet );
		}
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		for( EventHandler eventHandler : eventHandlers ) {
			eventHandler.onSessionCreated( sessionID );
		}
	}

}
