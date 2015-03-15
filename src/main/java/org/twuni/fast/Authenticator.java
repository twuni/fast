package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

public class Authenticator extends EventHandlerBase {

	private final WriteChannel writeChannel;
	private final byte [] credential;

	public Authenticator( byte [] credential, Session session ) {
		this( credential, session.write() );
	}

	public Authenticator( byte [] credential, WriteChannel writeChannel ) {
		this.credential = credential;
		this.writeChannel = writeChannel;
	}

	@Override
	public void onIdentityReceived( byte [] identity ) {
		writeChannel.fetch();
	}

	@Override
	public void onSessionCreated( byte [] sessionID ) {
		writeChannel.authenticate( credential );
	}

}
