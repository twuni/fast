package org.twuni.fast;

import java.util.List;

import org.twuni.fast.model.Packet;

/**
 * Provides packets from the beginning of a wrapped list.
 */
public class SimpleMailbox implements Mailbox {

	private final List<Packet> list;

	/**
	 * Initializes this provider to wrap the given {@code list}.
	 *
	 * @param list
	 *            the list to be wrapped.
	 */
	public SimpleMailbox( List<Packet> list ) {
		this.list = list;
	}

	@Override
	public Packet providePacket() {
		return list.isEmpty() ? null : list.remove( 0 );
	}

}
