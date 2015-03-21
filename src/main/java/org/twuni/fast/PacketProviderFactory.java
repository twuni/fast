package org.twuni.fast;

/**
 * Creates local packet provider instances for addresses.
 */
public interface PacketProviderFactory extends FAST {

	/**
	 * Creates a {@link PacketProvider} instance for the given {@code address}.
	 *
	 * @param address
	 *            the address to which packets provided by the returned provider
	 *            should belong.
	 * @return A provider for packets belonging to the given {@code address}.
	 */
	public PacketProvider createPacketProvider( byte [] address );

}
