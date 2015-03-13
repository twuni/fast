package org.twuni.fast.model;

import org.twuni.fast.FAST;

/**
 * FAST envelopes begin with a command followed by any additional parameters
 * required by that command. This interface contains constants for all of the
 * command codes used by the FAST protocol.
 */
public interface Command extends FAST {

	/**
	 * The ATTACH command is used to attach the local node to the remote node.
	 */
	public static final int ATTACH = 0x01;

	/**
	 * The AUTHENTICATE command is used to authenticate a node by providing a
	 * credential.
	 */
	public static final int AUTHENTICATE = 0x02;

	/**
	 * The IDENTIFY command is used to assign an identity to a remote endpoint.
	 */
	public static final int IDENTIFY = 0x03;

	/**
	 * The SESSION command is used to assign a session ID to the current
	 * connection between the local and remote endpoints.
	 */
	public static final int SESSION = 0x04;

	/**
	 * The FETCH command is used to request that any pending packets queued for
	 * delivery to the commander to be delivered immediately.
	 */
	public static final int FETCH = 0x05;

	/**
	 * The SEND command is used to send a packet to an address.
	 */
	public static final int SEND = 0x06;

	/**
	 * The REQUEST_ACKNOWLEDGMENT command is used to ask the remote endpoint how
	 * many packets it has received since this session was most recently
	 * eligible to send packets.
	 */
	public static final int REQUEST_ACKNOWLEDGMENT = 0x07;

	/**
	 * The ACKNOWLEDGMENT command is used to send an acknowledgment to the
	 * remote endpoint of the number of packets received during this session.
	 */
	public static final int ACKNOWLEDGE = 0x08;

	/**
	 * The DETACH command is used to explicitly detach the local and remote
	 * nodes.
	 */
	public static final int DETACH = 0xFF;

}
