package org.twuni.fast;

import java.io.PrintStream;

import org.twuni.fast.io.WriteChannel;

/**
 * This event handler implements the default behavior for a {@link Server}.
 */
public class ServerEventHandler extends EventHandlers {

	/**
	 * Initializes this event handler to notify the given {@code packetListener}
	 * of incoming/outgoing packets, to write to the given {@code channel}, to
	 * use the given {@code addressVerifier} for verifying that ATTACH commands
	 * are accepted only for known addresses. The given {@code sessionFactory}
	 * will be used when a new session needs to be created, and the given
	 * {@code authenticator} will be consulted to map a credential to an
	 * address owned by this credential. The given {@code packetProvider} will
	 * be used as the source of packets whenever a FETCH command is received.
	 *
	 * @param channel
	 *            the channel to which commands will be written.
	 * @param packetListener
	 *            the listener to be notified of incoming and outgoing packets.
	 * @param addressVerifier
	 *            the verifier to consult when determining whether to accept an
	 *            incoming ATTACH request for a given address.
	 * @param sessionFactory
	 *            the factory to use for creating sessions when a remote
	 *            endpoint is attempting to connect to a known local address.
	 * @param authenticator
	 *            the authenticator to use to verify the credential provided by
	 *            a remote endpoint and match that credential with an owned
	 *            address to which packets can be delivered.
	 * @param packetProviderFactory
	 *            the provider of packets to be sent when a FETCH command is
	 *            received.
	 * @param packetRouter
	 *            the router to use when trying to deliver packets.
	 * @param writeChannelProvider
	 *            the provider responsible for mapping addresses to write
	 *            channels.
	 * @param logger
	 *            the stream to which logging should occur.
	 */
	public ServerEventHandler( WriteChannel channel, PacketListener packetListener, AddressVerifier addressVerifier, SessionFactory sessionFactory, Authenticator authenticator, PacketProviderFactory packetProviderFactory, PacketRouter packetRouter, WriteChannelProvider writeChannelProvider, PrintStream logger ) {
		super( new ClojureEventLogger( logger ), new DetachOnException( channel ), new Reliability( channel ), new PacketListenerWrapper( packetListener ), new AttachableAddressFilter( addressVerifier ), new ServerSessionCreator( channel, sessionFactory ), new ServerAuthenticator( channel, authenticator ), new PacketDeliveryHandler( packetRouter ), new FlushPacketsOnFetch( channel, packetProviderFactory ), new WriteChannelManager( writeChannelProvider, channel ) );
	}

}
