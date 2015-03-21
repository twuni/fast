package org.twuni.fast;

import org.twuni.fast.exception.FASTException;
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
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onAcknowledgmentReceived( n );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onAcknowledgmentRequested() {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onAcknowledgmentRequested();
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onAttachRequested( address );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onConnected() {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onConnected();
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onCredentialReceived( credential );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onDisconnected() {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onDisconnected();
			}
		} catch( FASTException exception ) {
			onException( exception );
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
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onFetchRequested();
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onIdentityReceived( identity );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onPacketReceived( Packet packet ) {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onPacketReceived( packet );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onPacketSent( Packet packet ) {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onPacketSent( packet );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		try {
			for( EventHandler eventHandler : eventHandlers ) {
				eventHandler.onSessionCreated( sessionID );
			}
		} catch( FASTException exception ) {
			onException( exception );
		}
	}

}
