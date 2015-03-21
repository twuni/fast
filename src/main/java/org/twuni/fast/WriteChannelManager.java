package org.twuni.fast;

import org.twuni.fast.io.WriteChannel;

/**
 * Attaches and detaches {@link WriteChannel}s to/from a wrapped
 * {@link WriteChannelProvider} when the relevant events occur.
 */
public class WriteChannelManager extends EventHandlerBase {

	private final WriteChannelProvider channelProvider;
	private final WriteChannel channel;

	/**
	 * Initializes this manager to wrap the given {@code channelProvider} and to
	 * manage the given {@code channel}.
	 *
	 * @param channelProvider
	 *            the provider to which the given {@code channel} should be
	 *            attached or detached.
	 * @param channel
	 *            the channel to manage.
	 */
	public WriteChannelManager( WriteChannelProvider channelProvider, WriteChannel channel ) {
		this.channelProvider = channelProvider;
		this.channel = channel;
	}

	@Override
	public void onCredentialReceived( byte [] credential ) {
		channelProvider.attach( channel.getRemoteAddress(), channel );
	}

	@Override
	public void onDisconnected() {
		channelProvider.detach( channel.getRemoteAddress(), channel );
	}

}
