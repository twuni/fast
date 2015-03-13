package org.twuni.fast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import org.junit.Test;
import org.twuni.fast.io.SocketSession;
import org.twuni.fast.model.Packet;

public class SessionTest {

	@Test
	public void happyPath() throws IOException {

		Thread serverThread = new Thread() {

			@Override
			public void run() {

				try {

					ServerSocket server = ServerSocketFactory.getDefault().createServerSocket( 4857 );

					while( !Thread.interrupted() ) {

						Socket client = server.accept();

						final Session session = new SocketSession( client );
						final Reliability vault = new Reliability( session );

						session.setEventHandler( new EventHandler() {

							@Override
							public void onAcknowledgmentReceived( int n ) {
								vault.onAcknowledgmentReceived( n );
							}

							@Override
							public void onAcknowledgmentRequested() {
								vault.onAcknowledgmentRequested();
							}

							@Override
							public void onCredentialReceived( byte [] credential ) {
								vault.onPacketSent( new Packet( "bob@example.com", "Hi." ) );
								vault.onPacketSent( new Packet( "charlie@example.com", "Hey, do you want to meet for lunch?" ) );
								vault.onPacketSent( new Packet( "bob@example.com", "I lost my phone." ) );
								try {
									session.write().identify( "alice@example.com" );
								} catch( Throwable exception ) {
									onException( exception );
								}
							}

							@Override
							public void onConnected() {
								// Do nothing.
							}

							@Override
							public void onDisconnected() {
								try {
									session.write().detach();
								} catch( Throwable exception ) {
									onException( exception );
								}
							}

							@Override
							public void onFetchRequested() {
								vault.flush();
							}

							@Override
							public void onAttachRequested( byte [] address ) {
								try {
									session.write().session( "12345" );
								} catch( Throwable exception ) {
									onException( exception );
								}
							}

							@Override
							public void onPacketReceived( Packet packet ) {
								vault.onPacketReceived();
							}

							@Override
							public void onPacketSent( Packet packet ) {
								vault.onPacketSent( packet );
							}

							@Override
							public void onException( Throwable exception ) {
								System.out.print( String.format( "(error :type %s :message %s)", exception.getClass().getName(), exception.getLocalizedMessage() ) );
							}

							@Override
							public void onIdentityReceived( byte [] identity ) {
								// A server endpoint does nothing here.
							}

							@Override
							public void onSessionCreated( byte [] sessionID ) {
								// A server endpoint does nothing here.
							}

						} );

						session.read().accept().loopInBackground();

					}

				} catch( Throwable exception ) {
					// Ignore.
				}

			}

		};

		serverThread.start();

		Socket client = SocketFactory.getDefault().createSocket( "localhost", 4857 );

		final Session session = new SocketSession( client );
		final Reliability vault = new Reliability( session );

		session.setEventHandler( new ClojureEventAdapter() {

			@Override
			public void onSessionCreated( byte [] sessionID ) {
				super.onSessionCreated( sessionID );
				try {
					session.write().authenticate( "alice\np8ssw0rd" );
				} catch( Throwable exception ) {
					onException( exception );
				}
			}

			@Override
			public void onIdentityReceived( byte [] identity ) {
				super.onIdentityReceived( identity );
				try {
					session.write().fetch();
					Packet packet = new Packet( "bob@example.com", "Hello." );
					session.write().send( packet );
				} catch( Throwable exception ) {
					onException( exception );
				}
			}

			@Override
			public void onAcknowledgmentReceived( int n ) {
				super.onAcknowledgmentReceived( n );
				vault.onAcknowledgmentReceived( n );
			}

			@Override
			public void onAcknowledgmentRequested() {
				super.onAcknowledgmentRequested();
				vault.onAcknowledgmentRequested();
			}

			@Override
			public void onPacketSent( Packet packet ) {
				super.onPacketSent( packet );
				vault.onPacketSent( packet );
				try {
					session.write().requestAcknowledgment();
				} catch( Throwable exception ) {
					onException( exception );
				}
			}

			@Override
			public void onPacketReceived( Packet packet ) {
				super.onPacketReceived( packet );
				vault.onPacketReceived();
			}

		} );

		System.out.print( "(fast [" );

		session.write().connect();
		session.write().attach( "example.com" );
		session.read().loopInBackground();

		try {
			Thread.sleep( 1000 );
		} catch( InterruptedException ignore ) {
			// Ignore.
		}

		session.write().detach();

		System.out.print( "])" );

	}

}
