package org.twuni.fast.io;

import java.io.IOException;
import java.io.OutputStream;

import org.twuni.fast.EventHandler;
import org.twuni.fast.FAST;
import org.twuni.fast.exception.FASTWriteException;
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
	private byte [] sessionID;
	private byte [] localAddress;
	private byte [] remoteAddress;

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
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel attach( byte [] address ) {
		remoteAddress = address;
		try {
			output.write( Command.ATTACH );
			IOUtils.writeSmallBuffer( output, address );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Sends a greeting to the remote endpoint.
	 *
	 * @param address
	 *            the address on which the remote endpoint is expected to be
	 *            listening.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 * @see #attach(byte[])
	 */
	public WriteChannel attach( String address ) {
		return attach( address.getBytes() );
	}

	/**
	 * Authenticates to the remote endpoint with the given {@code credential}.
	 *
	 * @param credential
	 *            the credential to submit to the remote endpoint for
	 *            authentication.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel authenticate( byte [] credential ) {
		try {
			output.write( Command.AUTHENTICATE );
			IOUtils.writeSmallBuffer( output, credential );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Authenticates to the remote endpoint with the given {@code credential}.
	 *
	 * @param credential
	 *            the credential to submit to the remote endpoint for
	 *            authentication.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 * @see #authenticate(byte[])
	 */
	public WriteChannel authenticate( String credential ) {
		return authenticate( credential.getBytes() );
	}

	/**
	 * Identifies the output stream as a FAST channel by sending a FAST protocol
	 * header.
	 *
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 * @see FAST#FAST_HEADER
	 */
	public WriteChannel connect() {
		try {
			output.write( FAST_HEADER );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Explicitly detaches the session, if any.
	 *
	 * @return this object, for method chaining.
	 */
	public WriteChannel detach() {
		try {
			output.write( Command.DETACH );
			output.flush();
			output.close();
		} catch( IOException ignore ) {
			// Ignore.
		}
		return this;
	}

	/**
	 * Requests that the remote endpoint immediately deliver any pending
	 * messages queued for delivery to the local endpoint.
	 *
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel fetch() {
		try {
			output.write( Command.FETCH );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Returns the local address associated with this channel.
	 *
	 * @return the local address associated with this channel.
	 */
	public byte [] getLocalAddress() {
		return localAddress;
	}

	/**
	 * Returns the remote address associated with this channel.
	 *
	 * @return the remote address associated with this channel.
	 */
	public byte [] getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Returns the identifier for the session associated with this channel.
	 *
	 * @return the identifier for the session associated with this channel.
	 */
	public byte [] getSessionID() {
		return sessionID;
	}

	/**
	 * Accepts an authentication request, assigning the given {@code address} to
	 * the remote endpoint.
	 *
	 * @param address
	 *            the address to be assigned to the remote endpoint.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel identify( byte [] address ) {
		remoteAddress = address;
		try {
			output.write( Command.IDENTIFY );
			IOUtils.writeSmallBuffer( output, address );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Accepts an authentication request, assigning the given {@code address} to
	 * the remote endpoint.
	 *
	 * @param address
	 *            the address to be assigned to the remote endpoint.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 * @see #identify(byte[])
	 */
	public WriteChannel identify( String address ) {
		return identify( address.getBytes() );
	}

	/**
	 * Requests an acknowledgment of the number of packets received by the
	 * remote endpoint during this session.
	 *
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel requestAcknowledgment() {
		try {
			output.write( Command.REQUEST_ACKNOWLEDGMENT );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Sends each of the given packets to the remote endpoint. The address
	 * associated with each packet is expected to identify the intended
	 * recipient.
	 *
	 * @param packets
	 *            the packets to be sent.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel send( Packet... packets ) {
		try {
			for( Packet packet : packets ) {
				output.write( Command.SEND );
				PacketSerializer.write( packet, output );
				eventHandler.onPacketSent( packet );
			}
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Sends an acknowledgment that the local endpoint has received {@code n}
	 * packets from the remote endpoint during this session.
	 *
	 * @param n
	 *            the number of packets received during this session.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel sendAcknowledgment( int n ) {
		try {
			output.write( Command.ACKNOWLEDGE );
			IOUtils.writeInt( output, n );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Accepts a greeting, assigning the given {@code sessionID} to the remote
	 * endpoint.
	 *
	 * @param sessionID
	 *            the session ID to be assigned to the remote endpoint.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 */
	public WriteChannel session( byte [] sessionID ) {
		setSessionID( sessionID );
		try {
			output.write( Command.SESSION );
			IOUtils.writeSmallBuffer( output, sessionID );
			output.flush();
		} catch( IOException exception ) {
			throw new FASTWriteException( exception );
		}
		return this;
	}

	/**
	 * Accepts a greeting, assigning the given {@code sessionID} to the remote
	 * endpoint.
	 *
	 * @param sessionID
	 *            the session ID to be assigned to the remote endpoint.
	 * @return this object, for method chaining.
	 * @throws FASTWriteException
	 *             if a communications error occurs.
	 * @see #session(byte[])
	 */
	public WriteChannel session( String sessionID ) {
		return session( sessionID.getBytes() );
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

	/**
	 * Sets the local address associated with this channel.
	 *
	 * @param localAddress
	 *            the local address to be associated with this channel.
	 */
	public void setLocalAddress( byte [] localAddress ) {
		this.localAddress = localAddress;
	}

	/**
	 * Sets the identity for the session associated with this channel.
	 *
	 * @param sessionID
	 *            the identity for the session to be associated with this
	 *            channel.
	 */
	public void setSessionID( byte [] sessionID ) {
		this.sessionID = sessionID;
	}

}
