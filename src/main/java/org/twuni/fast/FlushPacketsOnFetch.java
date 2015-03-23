package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

/**
 * When a FETCH command is received, attempts to flush all queued packets from a
 * wrapped packet provider.
 */
public class FlushPacketsOnFetch extends EventHandlerBase {

	private final WriteChannel channel;
	private final MailboxFactory mailboxFactory;

	/**
	 * Initializes this handler to send packets along the given {@code channel},
	 * using the given {@code packetProvider} to find packets to send.
	 *
	 * @param channel
	 *            the channel along which to send fetched packets.
	 * @param mailboxFactory
	 *            the factory responsible for creating mailboxes.
	 */
	public FlushPacketsOnFetch( WriteChannel channel, MailboxFactory mailboxFactory ) {
		this.channel = channel;
		this.mailboxFactory = mailboxFactory;
	}

	@Override
	public void onFetchRequested() {
		Mailbox mailbox = mailboxFactory.createMailbox( channel.getRemoteAddress() );
		for( Packet packet = mailbox.providePacket(); packet != null; packet = mailbox.providePacket() ) {
			channel.send( packet );
		}
		channel.requestAcknowledgment();
	}

}
