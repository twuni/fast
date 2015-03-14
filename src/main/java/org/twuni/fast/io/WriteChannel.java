package org.twuni.fast.io;

import java.io.IOException;
import java.io.OutputStream;

import org.twuni.fast.EventHandler;
import org.twuni.fast.FAST;
import org.twuni.fast.model.Command;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.IOUtils;

/**
 * This helper object handles outgoing FAST communications over an underlying
 * stream.
 */
public class WriteChannel implements FAST {

	private final OutputStream output;
	private EventHandler eventHandler;

	/**
	 * Initializes this reader to write to the given {@code output} stream,
	 * without assigning an event handler.
	 *
	 * @param output
	 *            the stream to which data will be written.
	 */
	public WriteChannel( OutputStream output ) {
		this( output, null );
	}

	/**
	 * Initializes this reader to write to the given {@code output} stream,
	 * dispatching events to the given {@code eventHandler}.
	 *
	 * @param output
	 *            the stream to which data will be written.
	 * @param eventHandler
	 *            the recipient of any events which occur.
	 */
	public WriteChannel( OutputStream output, EventHandler eventHandler ) {
		this.output = output;
		this.eventHandler = eventHandler;
	}

	/**
	 * Sends a greeting to the remote endpoint.
	 *
	 * @param address
	 *            the address on which the remote endpoint is expected to be
	 *            listening.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void attach( byte [] address ) throws IOException {
		output.write( Command.ATTACH );
		IOUtils.writeSmallBuffer( output, address );
		output.flush();
	}

	/**
	 * Sends a greeting to the remote endpoint.
	 *
	 * @param address
	 *            the address on which the remote endpoint is expected to be
	 *            listening.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #attach(byte[])
	 */
	public void attach( String address ) throws IOException {
		attach( address.getBytes() );
	}

	/**
	 * Authenticates to the remote endpoint with the given {@code credential}.
	 *
	 * @param credential
	 *            the credential to submit to the remote endpoint for
	 *            authentication.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void authenticate( byte [] credential ) throws IOException {
		output.write( Command.AUTHENTICATE );
		IOUtils.writeSmallBuffer( output, credential );
		output.flush();
	}

	/**
	 * Authenticates to the remote endpoint with the given {@code credential}.
	 *
	 * @param credential
	 *            the credential to submit to the remote endpoint for
	 *            authentication.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #authenticate(byte[])
	 */
	public void authenticate( String credential ) throws IOException {
		authenticate( credential.getBytes() );
	}

	/**
	 * Identifies the output stream as a FAST channel by sending a FAST protocol
	 * header.
	 *
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see FAST#FAST_HEADER
	 */
	public void connect() throws IOException {
		output.write( FAST_HEADER );
		output.flush();
	}

	/**
	 * Explicitly detaches the session, if any.
	 */
	public void detach() {
		try {
			output.write( Command.DETACH );
			output.flush();
			output.close();
		} catch( IOException ignore ) {
			// Ignore.
		}
	}

	/**
	 * Requests that the remote endpoint immediately deliver any pending
	 * messages queued for delivery to the local endpoint.
	 *
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void fetch() throws IOException {
		output.write( Command.FETCH );
		output.flush();
	}

	/**
	 * Accepts an authentication request, assigning the given {@code address} to
	 * the remote endpoint.
	 *
	 * @param address
	 *            the address to be assigned to the remote endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void identify( byte [] address ) throws IOException {
		output.write( Command.IDENTIFY );
		IOUtils.writeSmallBuffer( output, address );
		output.flush();
	}

	/**
	 * Accepts an authentication request, assigning the given {@code address} to
	 * the remote endpoint.
	 *
	 * @param address
	 *            the address to be assigned to the remote endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #identify(byte[])
	 */
	public void identify( String address ) throws IOException {
		identify( address.getBytes() );
	}

	/**
	 * Requests an acknowledgment of the number of packets received by the
	 * remote endpoint during this session.
	 *
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void requestAcknowledgment() throws IOException {
		output.write( Command.REQUEST_ACKNOWLEDGMENT );
		output.flush();
	}

	/**
	 * Sends each of the given packets to the remote endpoint. The address
	 * associated with each packet is expected to identify the intended
	 * recipient.
	 *
	 * @param packets
	 *            the packets to be sent.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void send( Packet... packets ) throws IOException {
		output.write( Command.SEND );
		output.write( packets.length );
		for( Packet packet : packets ) {
			PacketSerializer.write( packet, output );
			eventHandler.onPacketSent( packet );
		}
		output.flush();
	}

	/**
	 * Sends an acknowledgment that the local endpoint has received {@code n}
	 * packets from the remote endpoint during this session.
	 *
	 * @param n
	 *            the number of packets received during this session.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void sendAcknowledgment( int n ) throws IOException {
		output.write( Command.ACKNOWLEDGE );
		IOUtils.writeInt( output, n );
		output.flush();
	}

	/**
	 * Accepts a greeting, assigning the given {@code sessionID} to the remote
	 * endpoint.
	 *
	 * @param sessionID
	 *            the session ID to be assigned to the remote endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void session( byte [] sessionID ) throws IOException {
		output.write( Command.SESSION );
		IOUtils.writeSmallBuffer( output, sessionID );
		output.flush();
	}

	/**
	 * Accepts a greeting, assigning the given {@code sessionID} to the remote
	 * endpoint.
	 *
	 * @param sessionID
	 *            the session ID to be assigned to the remote endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #session(byte[])
	 */
	public void session( String sessionID ) throws IOException {
		session( sessionID.getBytes() );
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
