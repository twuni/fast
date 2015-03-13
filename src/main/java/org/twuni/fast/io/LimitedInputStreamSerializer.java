package org.twuni.fast.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.twuni.fast.FAST;
import org.twuni.fast.util.IOUtils;

/**
 * This object handles serialization and deserialization of a
 * {@link LimitedInputStream} to/from a stream.
 */
public class LimitedInputStreamSerializer implements FAST {

	/**
	 * Reads a buffer from the given {@code input} stream and wraps it in a
	 * {@link LimitedInputStream}.
	 * 
	 * @param input
	 *            the stream from which the FAST packet will be read.
	 * @return the buffer read from the given {@code input} stream, wrapped in a
	 *         {@link LimitedInputStream}.
	 * @throws IOException
	 *             if an error occurs while attempting to read from the
	 *             given {@code input} stream.
	 * @see LimitedInputStream#LimitedInputStream(byte[])
	 * @see IOUtils#readBuffer(InputStream)
	 */
	public static LimitedInputStream read( InputStream input ) throws IOException {
		return new LimitedInputStream( IOUtils.readBuffer( input ) );
	}

	/**
	 * Writes the stream contained within the given {@code wrapper} to the given
	 * {@code output} stream.
	 * 
	 * @param wrapper
	 *            the wrapper describing the stream to be written.
	 * @param output
	 *            the stream to which the data will be written.
	 * @throws IOException
	 *             if an error occurs while writing the data to the given
	 *             {@code output} stream.
	 */
	public static void write( LimitedInputStream wrapper, OutputStream output ) throws IOException {
		
		InputStream stream = wrapper.getInputStream();
		int limit = wrapper.getLimit();

		output.write( IOUtils.toByteArray( limit ) );

		stream.mark( limit );
		IOUtils.pipe( stream, output );
		stream.reset();

	}

	private LimitedInputStreamSerializer() {
		// Prevent instances of this class from being constructed.
	}

}
