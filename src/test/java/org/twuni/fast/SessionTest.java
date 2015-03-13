package org.twuni.fast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.twuni.fast.exception.AuthenticationRejectedException;
import org.twuni.fast.exception.GreetingRejectedException;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

public class SessionTest {

	private static byte [] mockBytesReceived() throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		WriteChannel writer = new WriteChannel( out );

		writer.acceptGreeting( "12345" );
		writer.acceptAuthentication( "alice@example.com" );
		writer.send( new Packet( "bob@example.com", "Hi." ) );
		writer.requestAcknowledgment();
		writer.sendAcknowledgment( 1 );
		writer.disconnect();

		return out.toByteArray();

	}

	@Test
	public void blah() throws IOException, GreetingRejectedException, AuthenticationRejectedException {

		byte [] bytesReceived = mockBytesReceived();
		ByteArrayInputStream in = new ByteArrayInputStream( bytesReceived );
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Session session = new Session( in, out, new EventHandler() {

			@Override
			public void onPacketReceived( Packet packet ) {
				System.out.print( String.format( "(rx :timestamp %d :address %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getAddress() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) ) );
			}

			@Override
			public void onPacketSent( Packet packet ) {
				System.out.print( String.format( "(tx :timestamp %d :address %s :payload %d)", Long.valueOf( packet.getTimestamp() ), Arrays.toString( packet.getAddress() ).replaceAll( ",", "" ), Integer.valueOf( packet.getPayload().getLimit() ) ) );
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
			public void onGreetingReceived( byte [] address ) {
				System.out.print( String.format( "(greet %s)", Arrays.toString( address ).replaceAll( ",", "" ) ) );
			}

		} );

		System.out.print( "(session [" );
		session.greet( "example.com" );
		session.authenticate( toCredential( "alice", "p8ssw0rd" ) );
		session.fetch();
		Thread looper = session.loopInBackground();
		session.send( new Packet( "bob@example.com", "Hello." ) );
		session.requestAcknowledgment();
		session.sendAcknowledgment( 1 );
		session.disconnect();
		looper.interrupt();

		byte [] bytesSent = out.toByteArray();
		System.out.print( String.format( "](summary :tx [%d %s] :rx [%d %s])", Integer.valueOf( bytesSent.length ), Arrays.toString( bytesSent ).replaceAll( ",", "" ), Integer.valueOf( bytesReceived.length ), Arrays.toString( bytesReceived ).replaceAll( ",", "" ) ) );
		System.out.println( ")" );

	}

	private static byte [] toCredential( String identity, String secret ) {
		return toCredential( identity.getBytes(), secret.getBytes() );
	}

	private static byte [] toCredential( byte [] identity, byte [] secret ) {
		byte [] credential = new byte [identity.length + 1 + secret.length];
		for( int i = 0; i < identity.length; i++ ) {
			credential[i] = identity[i];
		}
		for( int i = 0; i < secret.length; i++ ) {
			credential[identity.length + 1 + i] = secret[i];
		}
		return credential;
	}

}
