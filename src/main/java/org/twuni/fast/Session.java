package org.twuni.fast;

import java.io.InputStream;
import java.io.OutputStream;

import org.twuni.fast.io.ReadChannel;
import org.twuni.fast.io.WriteChannel;

/**
 * A FAST session is the fundamental object for interacting with FAST services.
 */
public class Session implements FAST {

	private final ReadChannel reader;
	private final WriteChannel writer;

	/**
	 * Initializes a new FAST session between the given {@code input} stream and
	 * the given {@code output} stream without setting an event handler.
	 *
	 * @param input
	 *            the stream from which responses will be read.
	 * @param output
	 *            the stream to which requests will be written.
	 * @see #Session(InputStream, OutputStream, EventHandler)
	 */
	public Session( InputStream input, OutputStream output ) {
		this( input, output, null );
	}

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
		writer = new WriteChannel( output, eventHandler );
		if( eventHandler == null ) {
			setEventHandler( new ReliableEventHandler( writer ) );
		}
	}

	/**
	 * Returns the underlying read channel for this session.
	 *
	 * @return the underlying read channel for this session.
	 */
	public ReadChannel read() {
		return reader;
	}

	/**
	 * Assigns an event handler to this session to which events will be
	 * dispatched.
	 *
	 * @param eventHandler
	 *            the object to which events associated with this session will
	 *            be dispatched.
	 * @see ReadChannel#setEventHandler(EventHandler)
	 */
	public void setEventHandler( EventHandler eventHandler ) {
		reader.setEventHandler( eventHandler );
		writer.setEventHandler( eventHandler );
	}

	/**
	 * Returns the underlying write channel for this session.
	 *
	 * @return the underlying write channel for this session.
	 */
	public WriteChannel write() {
		return writer;
	}

}
