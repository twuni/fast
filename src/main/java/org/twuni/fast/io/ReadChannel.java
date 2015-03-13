package org.twuni.fast.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.twuni.fast.EventHandler;
import org.twuni.fast.FAST;
import org.twuni.fast.exception.AuthenticationRejectedException;
import org.twuni.fast.exception.GreetingRejectedException;
import org.twuni.fast.model.Command;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.IOUtils;

/**
 * This helper object handles incoming FAST communications over an underlying
 * stream.
 */
public class ReadChannel implements FAST {

	private final InputStream input;
	private final EventHandler eventHandler;

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
	 * Explicitly disconnects from the remote endpoint.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void disconnect() throws IOException {
		input.close();
		eventHandler.onDisconnected();
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
	public void executeCommand( int command ) throws IOException {
		switch( command ) {
			case Command.REQUEST_ACKNOWLEDGMENT:
				eventHandler.onAcknowledgmentRequested();
				break;
			case Command.ACKNOWLEDGE:
				int n = IOUtils.readInt( input );
				eventHandler.onAcknowledgmentReceived( n );
				break;
			case Command.AUTHENTICATE:
				byte [] credential = IOUtils.readSmallBuffer( input );
				eventHandler.onCredentialReceived( credential );
				break;
			case Command.DISCONNECT:
				disconnect();
				break;
			case Command.FETCH:
				eventHandler.onFetchRequested();
				break;
			case Command.SEND:
				int packetCount = input.read();
				for( int i = 0; i < packetCount; i++ ) {
					Packet packet = PacketSerializer.read( input );
					eventHandler.onPacketReceived( packet );
				}
				break;
			default:
		}
	}

	/**
	 * Attempts to read an address from the remote endpoint.
	 * 
	 * @return the address assigned to the local endpoint by the remote
	 *         endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @throws AuthenticationRejectedException
	 *             if no address was provided.
	 */
	public byte [] readAddress() throws IOException, AuthenticationRejectedException {
		byte [] address = IOUtils.readSmallBuffer( input );
		if( address.length <= 0 ) {
			throw new AuthenticationRejectedException();
		}
		return address;
	}

	/**
	 * Reads and executes the next command in the stream.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #readCommand()
	 * @see #executeCommand(int)
	 * @see Command
	 */
	public void readAndExecuteCommand() throws IOException {
		executeCommand( readCommand() );
	}

	/**
	 * Reads the next command from the underlying stream.
	 * 
	 * @return the next command in the underlying stream.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see Command
	 */
	public int readCommand() throws IOException {
		return input.read();
	}

	/**
	 * Reads a greeting from the remote endpoint. If the greeting does not
	 * contain the expected header, the channel is immediately disconnected.
	 * Otherwise, an address is read and dispatched to the channel's event
	 * handler.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void readGreeting() throws IOException {
		byte [] header = IOUtils.readFully( input, FAST_HEADER.length );
		if( !Arrays.equals( header, FAST_HEADER ) ) {
			disconnect();
			return;
		}
		byte [] address = IOUtils.readSmallBuffer( input );
		eventHandler.onGreetingReceived( address );
	}

	/**
	 * Attempts to read a session ID from the remote endpoint.
	 * 
	 * @return the identifier assigned by the remote endpoint to this session.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @throws GreetingRejectedException
	 *             if no session ID was provided.
	 */
	public byte [] readSessionID() throws IOException, GreetingRejectedException {
		byte [] sessionID = IOUtils.readSmallBuffer( input );
		if( sessionID.length <= 0 ) {
			throw new GreetingRejectedException();
		}
		return sessionID;
	}

}
