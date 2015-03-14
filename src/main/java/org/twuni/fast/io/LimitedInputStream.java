package org.twuni.fast.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.twuni.fast.FAST;

/**
 * Wraps an input stream of known length.
 */
public class LimitedInputStream implements FAST {

	private final InputStream inputStream;
	private final int limit;

	/**
	 * Initializes a new wrapper around the given {@code buffer}.
	 *
	 * @param buffer
	 *            a buffer containing the data to be wrapped.
	 * @see ByteArrayInputStream#ByteArrayInputStream(byte[])
	 * @see #LimitedInputStream(InputStream, int)
	 */
	public LimitedInputStream( byte [] buffer ) {
		this( new ByteArrayInputStream( buffer ), buffer.length );
	}

	/**
	 * Initializes a new wrapper around the given {@code buffer}.
	 *
	 * @param buffer
	 *            a buffer containing the data to be wrapped.
	 * @param offset
	 *            the offset within the {@code buffer} where the data begins.
	 * @param length
	 *            the length of the data contained within the buffer.
	 * @see ByteArrayInputStream#ByteArrayInputStream(byte[], int, int)
	 * @see #LimitedInputStream(InputStream, int)
	 */
	public LimitedInputStream( byte [] buffer, int offset, int length ) {
		this( new ByteArrayInputStream( buffer, offset, length ), length );
	}

	/**
	 * Initializes a new wrapper around the given {@code inputStream} with the
	 * given {@code size}.
	 *
	 * @param inputStream
	 *            a stream containing at least {@code size} bytes.
	 * @param limit
	 *            the number of bytes expected to be read from the given
	 *            {@code inputStream}.
	 */
	public LimitedInputStream( InputStream inputStream, int limit ) {
		this.inputStream = inputStream;
		this.limit = limit;
	}

	/**
	 * Returns the underlying input stream.
	 *
	 * @return the underlying input stream.
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Returns the limit for the number of bytes to be read from the underlying
	 * input stream.
	 *
	 * @return the limit for the number of bytes to be read from the underlying
	 *         input stream.
	 */
	public int getLimit() {
		return limit;
	}

}
