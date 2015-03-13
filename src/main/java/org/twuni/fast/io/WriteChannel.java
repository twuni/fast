package org.twuni.fast.io;

import java.io.IOException;
import java.io.OutputStream;

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

	/**
	 * Initializes this reader to write to the given {@code input} stream.
	 * 
	 * @param output
	 *            the stream to which data will be written.
	 */
	public WriteChannel( OutputStream output ) {
		this.output = output;
	}

	/**
	 * Accepts an authentication request, assigning the given {@code address} to
	 * the remote endpoint.
	 * 
	 * @param address
	 *            the address to be assigned to the remote endpoint.
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #acceptAuthentication(byte[])
	 */
	public void acceptAuthentication( String address ) throws IOException {
		acceptAuthentication( address.getBytes() );
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
	public void acceptAuthentication( byte [] address ) throws IOException {
		IOUtils.writeSmallBuffer( output, address );
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
	 * @see #acceptGreeting(byte[])
	 */
	public void acceptGreeting( String sessionID ) throws IOException {
		acceptGreeting( sessionID.getBytes() );
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
	public void acceptGreeting( byte [] sessionID ) throws IOException {
		IOUtils.writeSmallBuffer( output, sessionID );
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
	 */
	public void authenticate( byte [] credential ) throws IOException {
		output.write( Command.AUTHENTICATE );
		IOUtils.writeSmallBuffer( output, credential );
		output.flush();
	}

	/**
	 * Explicitly disconnects from the remote endpoint.
	 */
	public void disconnect() {
		try {
			output.write( Command.DISCONNECT );
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
	 * Sends a greeting to the remote endpoint.
	 * 
	 * @param address
	 *            the address on which the remote endpoint is expected to be
	 *            listening.
	 * @throws IOException
	 *             if a communications error occurs.
	 */
	public void greet( byte [] address ) throws IOException {
		output.write( FAST_HEADER );
		IOUtils.writeSmallBuffer( output, address );
		output.flush();
	}

	/**
	 * Rejects an authentication request from the remote endpoint, then
	 * disconnects the session.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #disconnect()
	 */
	public void rejectAuthentication() throws IOException {
		output.write( 0 );
		disconnect();
	}

	/**
	 * Rejects a greeting from the remote endpoint, then disconnects the
	 * session.
	 * 
	 * @throws IOException
	 *             if a communications error occurs.
	 * @see #disconnect()
	 */
	public void rejectGreeting() throws IOException {
		output.write( 0 );
		disconnect();
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

}
