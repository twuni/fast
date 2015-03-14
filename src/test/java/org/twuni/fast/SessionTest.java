package org.twuni.fast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import org.junit.Test;
import org.twuni.fast.io.SocketSession;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

public class SessionTest {

	static class ServerTestEventHandler extends EventHandlerBase {

		private final WriteChannel writeChannel;

		public ServerTestEventHandler( WriteChannel writeChannel ) {
			this.writeChannel = writeChannel;
		}

		public ServerTestEventHandler( Session session ) {
			this( session.write() );
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
		public void onAttachRequested( byte [] address ) {
			try {
				writeChannel.session( "12345" );
			} catch( Throwable exception ) {
				onException( exception );
			}
		}

		@Override
		public void onException( Throwable exception ) {
			System.out.print( String.format( "(error :type \"%s\" :message \"%s\")", exception.getClass().getName(), exception.getLocalizedMessage() ) );
		}

	}

	static class ClientTestEventHandler extends EventHandlerBase {

		private final WriteChannel writeChannel;

		public ClientTestEventHandler( WriteChannel writeChannel ) {
			this.writeChannel = writeChannel;
		}

		public ClientTestEventHandler( Session session ) {
			this( session.write() );
		}

		@Override
		public void onSessionCreated( byte [] sessionID ) {
			try {
				writeChannel.authenticate( "alice\np8ssw0rd" );
			} catch( Throwable exception ) {
				onException( exception );
			}
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

	}

	@Test
	public void happyPath() throws IOException {

		Thread serverThread = new Thread() {

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

		};

		serverThread.start();

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

}
