package org.twuni.fast.model;

import java.util.Arrays;

import org.twuni.fast.FAST;
import org.twuni.fast.io.LimitedInputStream;

/**
 * A FAST packet contains a payload and the metadata necessary to deliver it.
 */
public class Packet implements FAST {

	private final long timestamp;
	private final byte [] from;
	private final byte [] to;
	private final LimitedInputStream payload;

	/**
	 * Initializes a new FAST packet with the given {@code from}/{@code to}
	 * address and {@code payload}, with the timestamp set to the current time.
	 *
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 * @see System#currentTimeMillis()
	 * @see #Packet(byte[], byte[], LimitedInputStream)
	 */
	public Packet( byte [] from, byte [] to, byte [] payload ) {
		this( from, to, new LimitedInputStream( payload ) );
	}

	/**
	 * Initializes a new FAST packet with the given {@code from}/{@code to}
	 * address and {@code payload}, with the timestamp set to the current time.
	 *
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 * @see System#currentTimeMillis()
	 * @see #Packet(long, byte[], byte[], LimitedInputStream)
	 */
	public Packet( byte [] from, byte [] to, LimitedInputStream payload ) {
		this( System.currentTimeMillis(), from, to, payload );
	}

	/**
	 * Initializes a new FAST packet with the given {@code timestamp},
	 * {@code from}/{@code to} address, and {@code payload}.
	 *
	 * @param timestamp
	 *            The time, in milliseconds since Unix epoch, at which this
	 *            packet was composed.
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(long, byte[], byte[], LimitedInputStream)
	 */
	public Packet( long timestamp, byte [] from, byte [] to, byte [] payload ) {
		this( timestamp, from, to, new LimitedInputStream( payload ) );
	}

	/**
	 * Initializes a new FAST packet with the given {@code timestamp},
	 * {@code from}/{@code to} address, and {@code payload}.
	 *
	 * @param timestamp
	 *            The time, in milliseconds since Unix epoch, at which this
	 *            packet was composed.
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 */
	public Packet( long timestamp, byte [] from, byte [] to, LimitedInputStream payload ) {
		this.timestamp = timestamp;
		this.from = from;
		this.to = to;
		this.payload = payload;
	}

	/**
	 * Initializes a new FAST packet with the given {@code timestamp},
	 * {@code from}/{@code to} address, and {@code payload}.
	 *
	 * @param timestamp
	 *            The time, in milliseconds since Unix epoch, at which this
	 *            packet was composed.
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(long, byte[], byte[], byte[])
	 */
	public Packet( long timestamp, String from, String to, String payload ) {
		this( timestamp, from.getBytes(), to.getBytes(), payload.getBytes() );
	}

	/**
	 * Initializes a new FAST packet with the given {@code from}/{@code to}
	 * address and {@code payload}, with the timestamp set to the current time.
	 *
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(byte[], byte[], byte[])
	 */
	public Packet( String from, String to, byte [] payload ) {
		this( from.getBytes(), to.getBytes(), payload );
	}

	/**
	 * Initializes a new FAST packet with the given {@code from}/{@code to}
	 * address and {@code payload}, with the timestamp set to the current time.
	 *
	 * @param from
	 *            The address claiming to have composed this packet.
	 * @param to
	 *            The address to which this packet is intended.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(byte[], byte[], byte[])
	 */
	public Packet( String from, String to, String payload ) {
		this( from.getBytes(), to.getBytes(), payload.getBytes() );
	}

	/**
	 * Returns the address claiming to have composed this packet.
	 *
	 * @return the address claiming to have composed this packet.
	 */
	public byte [] getFrom() {
		return from;
	}

	/**
	 * Returns the data contained within this packet.
	 *
	 * @return the data contained within this packet.
	 */
	public LimitedInputStream getPayload() {
		return payload;
	}

	/**
	 * Returns the time, in milliseconds since Unix epoch, at which this packet
	 * was composed.
	 *
	 * @return the time, in milliseconds since Unix epoch, at which this packet
	 *         was composed.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the address to which this packet is intended.
	 *
	 * @return the address to which this packet is intended.
	 */
	public byte [] getTo() {
		return to;
	}

	/**
	 * Returns a JSON-formatted description of this packet, for debugging
	 * purposes.
	 *
	 * @return a JSON-formatted description of this packet, for debugging
	 *         purposes.
	 */
	public String toJSONString() {
		return String.format( "{\"timestamp\":%d,\"from\":%s,\"to\":%s,\"payload\":{\"limit\":%d}}", Long.valueOf( timestamp ), Arrays.toString( from ), Arrays.toString( to ), Integer.valueOf( payload.getLimit() ) );
	}

	/**
	 * Returns a description of this packet, for debugging purposes.
	 *
	 * @return a description of this packet, for debugging purposes.
	 */
	@Override
	public String toString() {
		return String.format( "%016x%08x%08x%08x", Long.valueOf( timestamp ), Integer.valueOf( Arrays.hashCode( from ) ), Integer.valueOf( Arrays.hashCode( to ) ), Integer.valueOf( payload.getLimit() ) );
	}

}
