package org.twuni.fast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import org.junit.Before;
import org.junit.Test;
import org.twuni.fast.io.SocketSession;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

public class SessionTest {

	static class ClientTestEventHandler extends EventHandlerBase {

		private final WriteChannel writeChannel;

		public ClientTestEventHandler( Session session ) {
			this( session.write() );
		}

		public ClientTestEventHandler( WriteChannel writeChannel ) {
			this.writeChannel = writeChannel;
		}

		@Override
		public void onIdentityReceived( byte [] identity ) {
			try {
				writeChannel.fetch();
				writeChannel.send( new Packet( "bob@example.com", "Hello." ) );
			} catch( Throwable exception ) {
				onException( exception );
			}
		}

		@Override
		public void onSessionCreated( byte [] sessionID ) {
			try {
				writeChannel.authenticate( "alice\np8ssw0rd" );
			} catch( Throwable exception ) {
				onException( exception );
			}
		}

	}

	static class ServerTestEventHandler extends EventHandlerBase {

		private final WriteChannel writeChannel;

		public ServerTestEventHandler( Session session ) {
			this( session.write() );
		}

		public ServerTestEventHandler( WriteChannel writeChannel ) {
			this.writeChannel = writeChannel;
		}

		@Override
		public void onAttachRequested( byte [] address ) {
			try {
				writeChannel.session( "12345" );
			} catch( Throwable exception ) {
				onException( exception );
			}
		}

		@Override
		public void onCredentialReceived( byte [] credential ) {
			try {
				writeChannel.identify( "alice@example.com" );
			} catch( Throwable exception ) {
				onException( exception );
			}
		}

		@Override
		public void onDisconnected() {
			try {
				writeChannel.detach();
			} catch( Throwable exception ) {
				onException( exception );
			}
		}

		@Override
		public void onException( Throwable exception ) {
			System.out.print( String.format( "(error :type \"%s\" :message \"%s\")", exception.getClass().getName(), exception.getLocalizedMessage() ) );
		}

	}

	@Test
	public void clientLearningTest() throws Exception {

		Client client = new Client.Builder().packetListener( new PacketListener() {

			@Override
			public void onPacketReceived( Packet packet ) {
				System.out.println( String.format( "R%s", packet ) );
			}

			@Override
			public void onPacketSent( Packet packet ) {
				System.out.println( String.format( "S%s", packet ) );
			}

		} ).secure( false ).host( "localhost" ).user( "alice" ).password( "p8ssw0rd" ).build();

		client.send( new Packet( "bob@example.com", "Hello, Bob!" ) );

		try {
			Thread.sleep( 50 );
		} catch( InterruptedException ignore ) {
			// Ignore.
		}

		client.close();

	}

	@Test
	public void happyPath() throws IOException {

		Socket client = SocketFactory.getDefault().createSocket( "localhost", 4857 );

		Session session = new SocketSession( client );

		session.setEventHandler( new EventHandlers( new ReliableEventHandler( session ), new ClojureEventAdapter( System.out ), new ClientTestEventHandler( session ) ) );

		System.out.print( "(fast [" );

		session.write().connect();
		session.write().attach( "example.com" );
		session.read().loopInBackground();

		try {
			Thread.sleep( 25 );
		} catch( InterruptedException ignore ) {
			// Ignore.
		}

		session.write().detach();

		System.out.print( "])" );

	}

	@Before
	public void startTestServer() {

		new Thread( getClass().getName() + " TestServer" ) {

			@Override
			public void run() {

				try {

					ServerSocket server = ServerSocketFactory.getDefault().createServerSocket( 4857 );

					while( !Thread.interrupted() ) {

						Socket client = server.accept();

						Session session = new SocketSession( client );

						session.setEventHandler( new EventHandlers( new ReliableEventHandler( session ) {

							@Override
							public void onFetchRequested() {
								onPacketSent( new Packet( "bob@example.com", "Hi." ) );
								super.onFetchRequested();
							}

						}, new ServerTestEventHandler( session ) ) );

						session.read().accept().loopInBackground();

					}

				} catch( Throwable exception ) {
					// Ignore.
				}

			}

		}.start();

	}

}
