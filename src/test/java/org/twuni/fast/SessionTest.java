package org.twuni.fast;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.twuni.fast.model.Packet;

public class SessionTest {

	private static Client alice() throws UnknownHostException, IOException {
		return client( "alice", "p8ssw0rd" );
	}

	private static Authenticator authenticator() {
		return new AutomaticAuthenticator();
	}

	private static Client bob() throws UnknownHostException, IOException {
		return client( "bob", "p8ssw0rd" );
	}

	private static Client client( CharSequence username, CharSequence password ) throws UnknownHostException, IOException {
		return new Client.Builder().secure( false ).host( "localhost" ).port( 4857 ).credential( username, password ).build();
	}

	private static Client eve() throws UnknownHostException, IOException {
		return client( "alice", "n85tyf@c3" );
	}

	private static void relax( long ms ) {
		try {
			Thread.sleep( ms );
		} catch( InterruptedException ignore ) {
			// Ignore.
		}
	}

	private static Server server() {
		return new Server.Builder().secure( false ).authenticator( authenticator() ).logger( System.out ).build();
	}

	private Server server;

	@Test
	public void clientLearningTest() throws Exception {

		Client alice = alice();
		relax( 10 );
		alice.send( new Packet( alice.getIdentity(), "bob@localhost".getBytes(), "Hello, Bob!".getBytes() ) );
		relax( 50 );
		alice.close();

		Client bob = bob();
		relax( 10 );
		bob.send( new Packet( bob.getIdentity(), "alice@localhost".getBytes(), "Hi, Alice.".getBytes() ) );
		relax( 50 );
		bob.close();

		alice = alice();
		relax( 10 );
		alice.close();

		Client eve = eve();
		relax( 10 );
		eve.close();

	}

	@Before
	public void startTestServer() {
		server = server();
		server.startListening();
	}

	@After
	public void stopTestServer() {
		if( server != null ) {
			server.stopListening();
			server = null;
		}
	}

}
