package org.twuni.fast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;

import org.twuni.fast.exception.AuthenticationRejectedException;
import org.twuni.fast.exception.GreetingRejectedException;
import org.twuni.fast.io.ReadChannel;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.Validation;

/**
 * A FAST session is the fundamental object for interacting with FAST services.
 */
public class Session implements FAST {

	private static class Looper implements Runnable {

		private final Session session;

		public Looper( Session session ) {
			this.session = session;
		}

		@Override
		public void run() {
			try {
				session.loop();
			} catch( IOException exception ) {
				// Ignore.
			}
		}
	}

	private final ReadChannel reader;
	private final WriteChannel writer;
	private final EventHandler eventHandler;

	/**
	 * Initializes a new FAST session between the given {@code input} stream and
	 * the given {@code output} stream, dispatching events to the given
	 * {@code eventHandler}.
	 * 
	 * @param input
	 *            the stream from which responses will be read.
	 * @param output
	 *            the stream to which requests will be written.
	 * @param eventHandler
	 *            the recipient of events dispatched during this session.
	 */
	public Session( InputStream input, OutputStream output, EventHandler eventHandler ) {
		reader = new ReadChannel( input, eventHandler );
		writer = new WriteChannel( output );
		this.eventHandler = eventHandler;
	}

	/**
	 * Sends a greeting to the remote endpoint, expecting a session ID in
	 * response.
	 * 
	 * @param address
	 *            the address on which the remote endpoint is expected to be
	 *            listening.
	 * @return an identifier for this session.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @throws IllegalArgumentException
	 *             if the given {@code address} is {@code null} or is not
	 *             between 1-255 bytes in length.
	 * @throws GreetingRejectedException
	 *             if the remote endpoint rejects the greeting, probably because
	 *             it is not listening at the {@code address} indicated in the
	 *             greeting.
	 */
	public byte [] greet( byte [] address ) throws IOException, GreetingRejectedException {
		Validation.assertLengthBetween( 0x01, address, 0xFF, "address" );
		writer.greet( address );
		return reader.readSessionID();
	}

	/**
	 * Sends a greeting to the remote endpoint, expecting a session ID in
	 * response.
	 * 
	 * @param address
	 *            the address on which the remote endpoint is expected to be
	 *            listening.
	 * @return an identifier for this session.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @throws IllegalArgumentException
	 *             if the given {@code address} is {@code null} or is not
	 *             between 1-255 bytes in length.
	 * @throws GreetingRejectedException
	 *             if the remote endpoint rejects the greeting, probably because
	 *             it is not listening at the {@code address} indicated in the
	 *             greeting.
	 * @see #greet(byte[])
	 */
	public byte [] greet( String address ) throws IOException, GreetingRejectedException {
		return greet( address.getBytes() );
	}

	/**
	 * Authenticates to the remote endpoint with the given {@code credential}.
	 * 
	 * @param credential
	 *            the credential to submit to the remote endpoint for
	 *            authentication.
	 * @return the address of the local endpoint as it is known to the remote
	 *         endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @throws IllegalArgumentException
	 *             if the given {@code credential} is {@code null} or is not
	 *             between 1-255 bytes in length.
	 * @throws AuthenticationRejectedException
	 *             if the remote endpoint rejects the authentication, probably
	 *             because the credential is invalid.
	 * @see WriteChannel#authenticate(byte[])
	 * @see ReadChannel#readAddress()
	 */
	public byte [] authenticate( byte [] credential ) throws IOException, AuthenticationRejectedException {
		Validation.assertLengthBetween( 0x01, credential, 0xFF, "credential" );
		writer.authenticate( credential );
		return reader.readAddress();
	}

	/**
	 * Authenticates to the remote endpoint with the given {@code credential}.
	 * 
	 * @param credential
	 *            the credential to submit to the remote endpoint for
	 *            authentication.
	 * @return the address of the local endpoint as it is known to the remote
	 *         endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @throws IllegalArgumentException
	 *             if the given {@code credential} is {@code null} or is not
	 *             between 1-255 bytes in length.
	 * @throws AuthenticationRejectedException
	 *             if the remote endpoint rejects the authentication, probably
	 *             because the credential is invalid.
	 * @see #authenticate(byte[])
	 */
	public byte [] authenticate( String credential ) throws IOException, AuthenticationRejectedException {
		return authenticate( credential.getBytes() );
	}

	/**
	 * Requests that the remote endpoint immediately deliver any pending
	 * messages queued for delivery to the local endpoint.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see WriteChannel#fetch()
	 */
	public void fetch() throws IOException {
		writer.fetch();
	}

	/**
	 * Enters a read-execute loop on commands sent by the remote endpoint.
	 * 
	 * @throws IOException
	 *             if/when a communications error occurs.
	 */
	public void loop() throws IOException {
		while( true ) {
			if( Thread.interrupted() ) {
				break;
			}
			reader.readAndExecuteCommand();
		}
	}

	/**
	 * Spawns a new thread to call {@link #loop()} on this object, starts the
	 * thread, and returns the created thread.
	 * 
	 * @return the thread created to {@link #loop()} this object.
	 * @see #looper()
	 * @see #loop()
	 */
	public Thread loopInBackground() {
		Thread thread = new Thread( looper() );
		thread.start();
		return thread;
	}

	/**
	 * Initializes a new {@link Runnable} object that simply calls
	 * {@link #loop()} on this object -- useful for spawning new {@link Thread}s
	 * {@link TimerTask}s, etc.
	 * 
	 * @return a new {@link Runnable} object that simply calls {@link #loop()}
	 *         on this object until either an exception occurs or its thread is
	 *         interrupted.
	 */
	public Runnable looper() {
		return new Looper( this );
	}

	/**
	 * Sends each of the given packets to the remote endpoint. The address
	 * associated with each packet is expected to identify the intended
	 * recipient. After all packets have been sent,
	 * {@link EventHandler#onPacketSent(Packet)} is called for each
	 * of the given packets.
	 * 
	 * @param packets
	 *            the packets to be sent.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see WriteChannel#send(Packet...)
	 * @see EventHandler#onPacketSent(Packet)
	 */
	public void send( Packet... packets ) throws IOException {
		writer.send( packets );
		for( Packet packet : packets ) {
			eventHandler.onPacketSent( packet );
		}
	}

	/**
	 * Explicitly disconnects from the remote endpoint.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see ReadChannel#disconnect()
	 * @see WriteChannel#disconnect()
	 */
	public void disconnect() throws IOException {
		reader.disconnect();
		writer.disconnect();
	}

	/**
	 * Requests an acknowledgment of the number of packets received by the
	 * remote endpoint during this session.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see WriteChannel#requestAcknowledgment()
	 */
	public void requestAcknowledgment() throws IOException {
		writer.requestAcknowledgment();
	}

	/**
	 * Sends an acknowledgment that the local endpoint has received {@code n}
	 * packets from the remote endpoint during this session.
	 * 
	 * @param n
	 *            the number of packets received during this session.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see WriteChannel#sendAcknowledgment(int)
	 */
	public void sendAcknowledgment( int n ) throws IOException {
		writer.sendAcknowledgment( n );
	}

}
