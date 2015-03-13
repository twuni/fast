package org.twuni.fast.model;

import org.twuni.fast.FAST;

/**
 * FAST envelopes begin with a command followed by any additional parameters
 * required by that command. This interface contains constants for all of the
 * command codes used by the FAST protocol.
 */
public interface Command extends FAST {

	/**
	 * The AUTHENTICATE command is used to authenticate a client or server by
	 * providing a credential.
	 */
	public static final int AUTHENTICATE = 0x01;

	/**
	 * The FETCH command is used to request that any pending packets queued for
	 * delivery to the commander to be delivered immediately.
	 */
	public static final int FETCH = 0x02;

	/**
	 * The SEND command is used to send a packet to an address.
	 */
	public static final int SEND = 0x03;

	/**
	 * The REQUEST_ACKNOWLEDGMENT command is used to ask the remote endpoint how
	 * many packets it has received since this session was most recently
	 * eligible to send packets.
	 */
	public static final int REQUEST_ACKNOWLEDGMENT = 0x04;

	/**
	 * The ACKNOWLEDGMENT command is used to send an acknowledgment to the
	 * remote endpoint of the number of packets received during this session.
	 */
	public static final int ACKNOWLEDGE = 0x05;

	/**
	 * The DISCONNECT command is used to explicitly disconnect from the service.
	 */
	public static final int DISCONNECT = 0xFF;

}
