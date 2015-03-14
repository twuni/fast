package org.twuni.fast;

import org.twuni.fast.model.Packet;

/**
 * An event handler is notified of events which have occurred during a
 * session.
 */
public interface EventHandler extends FAST {

	/**
	 * This method is called whenever a remote endpoint has acknowledged that it
	 * has received {@code n} packets during this session.
	 *
	 * @param n
	 *            The number of packets being acknowledged.
	 */
	public void onAcknowledgmentReceived( int n );

	/**
	 * This method is called whenever a remote endpoint has requested an
	 * acknowledgment of the number of packets received during this session.
	 */
	public void onAcknowledgmentRequested();

	/**
	 * This method is called whenever a remote endpoint has attempted to
	 * establish a session at the given {@code address}.
	 *
	 * @param address
	 *            the local address to which the remote endpoint has indicated a
	 *            desire to connect.
	 */
	public void onAttachRequested( byte [] address );

	/**
	 * This method is called just after a session has connected.
	 */
	public void onConnected();

	/**
	 * This method is called whenever a remote endpoint has submitted a
	 * {@code credential} for authentication.
	 *
	 * @param credential
	 *            the credential received from the remote endpoint for
	 *            authentication.
	 */
	public void onCredentialReceived( byte [] credential );

	/**
	 * This method is called just after a session has disconnected.
	 */
	public void onDisconnected();

	/**
	 * This method is called if anything goes wrong within a session.
	 *
	 * @param exception
	 *            what went wrong.
	 */
	public void onException( Throwable exception );

	/**
	 * This method is called whenever a remote endpoint has requested immediate
	 * delivery of any packets addressed to it.
	 */
	public void onFetchRequested();

	/**
	 * This method is called whenever a remote endpoint has assigned an
	 * {@code identity} to the local endpoint.
	 *
	 * @param identity
	 *            the identity assigned to the local endpoint by the remote
	 *            endpoint.
	 */
	public void onIdentityReceived( byte [] identity );

	/**
	 * This method is called just after a packet is received.
	 *
	 * @param packet
	 *            the received packet.
	 */
	public void onPacketReceived( Packet packet );

	/**
	 * This method is called just after a packet has been sent.
	 *
	 * @param packet
	 *            the sent packet.
	 */
	public void onPacketSent( Packet packet );

	/**
	 * This method is called whenever a session has been created.
	 *
	 * @param sessionID
	 *            the identifier assigned by the remote endpoint to this
	 *            session.
	 */
	public void onSessionCreated( byte [] sessionID );

}
