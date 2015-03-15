package org.twuni.fast.model;

import java.util.Arrays;

import org.twuni.fast.FAST;
import org.twuni.fast.io.LimitedInputStream;

/**
 * A FAST packet contains a payload and the metadata necessary to deliver it.
 */
public class Packet implements FAST {

	private final long timestamp;
	private final byte [] address;
	private final LimitedInputStream payload;

	/**
	 * Initializes a new FAST packet with the given {@code address} and
	 * {@code payload}, with the timestamp set to the current time.
	 *
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 * @see System#currentTimeMillis()
	 * @see #Packet(byte[], LimitedInputStream)
	 */
	public Packet( byte [] address, byte [] payload ) {
		this( address, new LimitedInputStream( payload ) );
	}

	/**
	 * Initializes a new FAST packet with the given {@code address} and
	 * {@code payload}, with the timestamp set to the current time.
	 *
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 * @see System#currentTimeMillis()
	 * @see #Packet(long, byte[], LimitedInputStream)
	 */
	public Packet( byte [] address, LimitedInputStream payload ) {
		this( System.currentTimeMillis(), address, payload );
	}

	/**
	 * Initializes a new FAST packet with the given {@code timestamp},
	 * {@code address}, and {@code payload}.
	 *
	 * @param timestamp
	 *            The time, in milliseconds since Unix epoch, at which this
	 *            packet was composed.
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(long, byte[], LimitedInputStream)
	 */
	public Packet( long timestamp, byte [] address, byte [] payload ) {
		this( timestamp, address, new LimitedInputStream( payload ) );
	}

	/**
	 * Initializes a new FAST packet with the given {@code timestamp},
	 * {@code address}, and {@code payload}.
	 *
	 * @param timestamp
	 *            The time, in milliseconds since Unix epoch, at which this
	 *            packet was composed.
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 */
	public Packet( long timestamp, byte [] address, LimitedInputStream payload ) {
		this.timestamp = timestamp;
		this.address = address;
		this.payload = payload;
	}

	/**
	 * Initializes a new FAST packet with the given {@code timestamp},
	 * {@code address}, and {@code payload}.
	 *
	 * @param timestamp
	 *            The time, in milliseconds since Unix epoch, at which this
	 *            packet was composed.
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(long, byte[], byte[])
	 */
	public Packet( long timestamp, String address, String payload ) {
		this( timestamp, address.getBytes(), payload.getBytes() );
	}

	/**
	 * Initializes a new FAST packet with the given {@code address} and
	 * {@code payload}, with the timestamp set to the current time.
	 *
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(byte[], byte[])
	 */
	public Packet( String address, byte [] payload ) {
		this( address.getBytes(), payload );
	}

	/**
	 * Initializes a new FAST packet with the given {@code address} and
	 * {@code payload}, with the timestamp set to the current time.
	 *
	 * @param address
	 *            The address to which the packet belongs.
	 * @param payload
	 *            The data contained within the packet.
	 * @see #Packet(byte[], byte[])
	 */
	public Packet( String address, String payload ) {
		this( address.getBytes(), payload.getBytes() );
	}

	/**
	 * Returns the address to which this packet belongs.
	 *
	 * @return the address to which this packet belongs.
	 */
	public byte [] getAddress() {
		return address;
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
	 * Returns a JSON-formatted description of this packet, for debugging
	 * purposes.
	 *
	 * @return a JSON-formatted description of this packet, for debugging
	 *         purposes.
	 */
	public String toJSONString() {
		return String.format( "{\"timestamp\":%d,\"address\":%s,\"payload\":{\"limit\":%d}}", Long.valueOf( timestamp ), Arrays.toString( address ), Integer.valueOf( payload.getLimit() ) );
	}

	/**
	 * Returns a description of this packet, for debugging purposes.
	 *
	 * @return a description of this packet, for debugging purposes.
	 */
	@Override
	public String toString() {
		return String.format( "%016x%08x%08x", Long.valueOf( timestamp ), Integer.valueOf( Arrays.hashCode( address ) ), Integer.valueOf( payload.getLimit() ) );
	}

}
