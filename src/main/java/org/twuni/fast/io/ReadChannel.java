package org.twuni.fast.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.TimerTask;

import org.twuni.fast.EventHandler;
import org.twuni.fast.FAST;
import org.twuni.fast.exception.FASTReadException;
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
			} catch( FASTReadException ignore ) {
				// Terminate the loop.
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
	 * @see EventHandler#onConnected()
	 * @see #disconnect()
	 */
	public ReadChannel accept() {
		try {
			byte [] header = IOUtils.readFully( input, FAST_HEADER.length );
			if( !Arrays.equals( header, FAST_HEADER ) ) {
				executeDetachCommand();
				throw new FASTReadException();
			}
			eventHandler.onConnected();
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
		return this;
	}

	/**
	 * Explicitly disconnects from the remote endpoint.
	 *
	 * @return this object, for chaining commands together.
	 */
	public ReadChannel disconnect() {
		try {
			input.close();
		} catch( IOException ignore ) {
			// Ignore.
		}
		eventHandler.onDisconnected();
		return this;
	}

	private void executeAcknowledgmentCommand() {
		try {
			int n = IOUtils.readInt( input );
			eventHandler.onAcknowledgmentReceived( n );
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
	}

	private void executeAttachCommand() {
		try {
			byte [] address = IOUtils.readSmallBuffer( input );
			eventHandler.onAttachRequested( address );
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
	}

	private void executeAuthenticateCommand() {
		try {
			byte [] credential = IOUtils.readSmallBuffer( input );
			eventHandler.onCredentialReceived( credential );
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
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
	private void executeCommand( int command ) {
		switch( command ) {
			case Command.ACKNOWLEDGE:
				executeAcknowledgmentCommand();
				break;
			case Command.ATTACH:
				executeAttachCommand();
				break;
			case Command.AUTHENTICATE:
				executeAuthenticateCommand();
				break;
			case Command.DETACH:
				executeDetachCommand();
				break;
			case Command.FETCH:
				executeFetchCommand();
				break;
			case Command.IDENTIFY:
				executeIdentifyCommand();
				break;
			case Command.REQUEST_ACKNOWLEDGMENT:
				executeRequestAcknowledgmentCommand();
				break;
			case Command.SEND:
				executeSendCommand();
				break;
			case Command.SESSION:
				executeSessionCommand();
				break;
			default:
		}
	}

	private void executeDetachCommand() {
		disconnect();
	}

	private void executeFetchCommand() {
		eventHandler.onFetchRequested();
	}

	private void executeIdentifyCommand() {
		try {
			byte [] identity = IOUtils.readSmallBuffer( input );
			eventHandler.onIdentityReceived( identity );
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
	}

	private void executeRequestAcknowledgmentCommand() {
		eventHandler.onAcknowledgmentRequested();
	}

	private void executeSendCommand() {
		try {
			int packetCount = input.read();
			for( int i = 0; i < packetCount; i++ ) {
				Packet packet = PacketSerializer.read( input );
				eventHandler.onPacketReceived( packet );
			}
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
	}

	private void executeSessionCommand() {
		try {
			byte [] sessionID = IOUtils.readSmallBuffer( input );
			eventHandler.onSessionCreated( sessionID );
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
	}

	private String getLooperThreadName() {
		return String.format( "%s(%x)", Looper.class.getName(), Integer.valueOf( hashCode() ) );
	}

	/**
	 * Enters a read-execute loop on commands sent by the remote endpoint until
	 * either the thread is interrupted or an exception occurs.
	 *
	 * @return this object, for chaining commands together.
	 * @see #next()
	 */
	public ReadChannel loop() {
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
		Thread thread = new Thread( looper(), getLooperThreadName() );
		thread.start();
		return thread;
	}

	/**
	 * Reads and executes the next command in the stream.
	 *
	 * @return this object, for chaining commands together.
	 * @see #readCommand()
	 * @see #executeCommand(int)
	 * @see Command
	 */
	public ReadChannel next() {
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
	private int readCommand() {
		try {
			return input.read();
		} catch( IOException exception ) {
			throw new FASTReadException( exception );
		}
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
