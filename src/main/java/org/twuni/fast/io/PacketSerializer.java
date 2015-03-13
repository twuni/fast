package org.twuni.fast.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.twuni.fast.FAST;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.IOUtils;

/**
 * This object handles serialization and deserialization of a {@link Packet}
 * to/from a stream.
 */
public class PacketSerializer implements FAST {

	/**
	 * Reads a FAST packet from the given {@code input} stream.
	 * 
	 * @param input
	 *            the stream from which the FAST packet will be read.
	 * @return the FAST packet read from the given {@code input} stream.
	 * @throws IOException
	 *             if an error occurs while attempting to read a packet from the
	 *             given {@code input} stream.
	 */
	public static Packet read( InputStream input ) throws IOException {
		long timestamp = IOUtils.readLong( input );
		byte [] address = IOUtils.readSmallBuffer( input );
		LimitedInputStream payload = LimitedInputStreamSerializer.read( input );
		return new Packet( timestamp, address, payload );
	}

	/**
	 * Writes the given {@code packet} to the given {@code output} stream.
	 * 
	 * @param packet
	 *            the packet to be written.
	 * @param output
	 *            the stream to which the packet will be written.
	 * @throws IOException
	 *             if an error occurs while writing the packet to the given
	 *             {@code output} stream.
	 */
	public static void write( Packet packet, OutputStream output ) throws IOException {
		IOUtils.writeLong( output, packet.getTimestamp() );
		IOUtils.writeSmallBuffer( output, packet.getAddress() );
		LimitedInputStreamSerializer.write( packet.getPayload(), output );
	}

	private PacketSerializer() {
		// Prevent instances of this class from being constructed.
	}

}
