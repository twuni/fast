package org.twuni.fast.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.TimerTask;

import org.twuni.fast.EventHandler;
import org.twuni.fast.FAST;
import org.twuni.fast.model.Command;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.IOUtils;

/**
 * This helper object handles incoming FAST communications over an underlying
 * stream.
 */
public class ReadChannel implements FAST {

	private static class Looper implements Runnable {

		private final ReadChannel channel;

		public Looper( ReadChannel channel ) {
			this.channel = channel;
		}

		@Override
		public void run() {
			try {
				channel.loop();
			} catch( IOException exception ) {
				// Ignore.
			}
		}
	}

	private final InputStream input;

	private EventHandler eventHandler;

	/**
	 * Initializes this reader to read from the given {@code input} stream,
	 * without assigning an event handler.
	 * 
	 * @param input
	 *            the stream from which to read.
	 * @see #ReadChannel(InputStream, EventHandler)
	 */
	public ReadChannel( InputStream input ) {
		this( input, null );
	}

	/**
	 * Initializes this reader to read from the given {@code input} stream,
	 * dispatching events to the given {@code eventHandler}.
	 * 
	 * @param input
	 *            the stream from which to read.
	 * @param eventHandler
	 *            the recipient of any events which occur.
	 */
	public ReadChannel( InputStream input, EventHandler eventHandler ) {
		this.input = input;
		this.eventHandler = eventHandler;
	}

	/**
	 * Attempts to read a FAST protocol header from the underlying stream. If
	 * successful, dispatches to {@link EventHandler#onConnected()}. Otherwise,
	 * triggers a disconnection.
	 * 
	 * @return this object, for chaining commands together.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see EventHandler#onConnected()
	 * @see #disconnect()
	 */
	public ReadChannel accept() throws IOException {
		byte [] header = IOUtils.readFully( input, FAST_HEADER.length );
		if( !Arrays.equals( header, FAST_HEADER ) ) {
			return disconnect();
		}
		eventHandler.onConnected();
		return this;
	}

	/**
	 * Explicitly disconnects from the remote endpoint.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public ReadChannel disconnect() throws IOException {
		input.close();
		eventHandler.onDisconnected();
		return this;
	}

	/**
	 * Executes the given {@code command}.
	 * 
	 * @param command
	 *            the command to be executed.
	 * @throws IOException
	 *             if a communications error occurs while attempting to execute
	 *             the command.
	 * @see Command
	 */
	private void executeCommand( int command ) throws IOException {
		switch( command ) {
			case Command.ACKNOWLEDGE:
				int n = IOUtils.readInt( input );
				eventHandler.onAcknowledgmentReceived( n );
				break;
			case Command.ATTACH:
				byte [] address = IOUtils.readSmallBuffer( input );
				eventHandler.onAttachRequested( address );
				break;
			case Command.AUTHENTICATE:
				byte [] credential = IOUtils.readSmallBuffer( input );
				eventHandler.onCredentialReceived( credential );
				break;
			case Command.DETACH:
				disconnect();
				break;
			case Command.FETCH:
				eventHandler.onFetchRequested();
				break;
			case Command.IDENTIFY:
				byte [] identity = IOUtils.readSmallBuffer( input );
				eventHandler.onIdentityReceived( identity );
				break;
			case Command.REQUEST_ACKNOWLEDGMENT:
				eventHandler.onAcknowledgmentRequested();
				break;
			case Command.SEND:
				int packetCount = input.read();
				for( int i = 0; i < packetCount; i++ ) {
					Packet packet = PacketSerializer.read( input );
					eventHandler.onPacketReceived( packet );
				}
				break;
			case Command.SESSION:
				byte [] sessionID = IOUtils.readSmallBuffer( input );
				eventHandler.onSessionCreated( sessionID );
				break;
			default:
		}
	}

	/**
	 * Enters a read-execute loop on commands sent by the remote endpoint until
	 * either the thread is interrupted or an exception occurs.
	 * 
	 * @return this object, for chaining commands together.
	 * @throws IOException
	 *             if/when a communications error occurs.
	 * @see #next()
	 */
	public ReadChannel loop() throws IOException {
		while( !Thread.interrupted() ) {
			next();
		}
		return this;
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
	 * Reads and executes the next command in the stream.
	 * 
	 * @return this object, for chaining commands together.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #readCommand()
	 * @see #executeCommand(int)
	 * @see Command
	 */
	public ReadChannel next() throws IOException {
		executeCommand( readCommand() );
		return this;
	}

	/**
	 * Reads the next command from the underlying stream.
	 * 
	 * @return the next command in the underlying stream.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see Command
	 */
	private int readCommand() throws IOException {
		return input.read();
	}

	/**
	 * Assigns an event handler to this session to which events will be
	 * dispatched.
	 * 
	 * @param eventHandler
	 *            the object to which events associated with this session will
	 *            be dispatched.
	 */
	public void setEventHandler( EventHandler eventHandler ) {
		this.eventHandler = eventHandler;
	}

}
