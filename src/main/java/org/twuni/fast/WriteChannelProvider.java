package org.twuni.fast;

import java.util.Set;

import org.twuni.fast.io.WriteChannel;

/**
 * A write channel provider is responsible for mapping addresses to one or more
 * {@link WriteChannel}s.
 */
public interface WriteChannelProvider {

	/**
	 * Attach the given {@code channel} to the given {@code address}.
	 *
	 * @param address
	 *            the address to which the channel should be attached.
	 * @param channel
	 *            the channel to attach.
	 */
	public void attach( byte [] address, WriteChannel channel );

	/**
	 * Detaches the given {@code channel} from the given {@code address}.
	 *
	 * @param address
	 *            the address from which the channel should be detached.
	 * @param channel
	 *            the channel to detach.
	 */
	public void detach( byte [] address, WriteChannel channel );

	/**
	 * Provides a set of write channels associated with the given
	 * {@code address}.
	 *
	 * @param address
	 *            the address for which to provide write channels.
	 * @return a set of write channels attached to the given {@code address}.
	 */
	public Set<WriteChannel> provideWriteChannels( byte [] address );

}
