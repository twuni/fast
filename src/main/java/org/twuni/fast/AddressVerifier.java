package org.twuni.fast;

import org.twuni.fast.exception.FASTUnknownAddressException;

/**
 * Verifies whether a node is associated with an address.
 */
public interface AddressVerifier {

	/**
	 * Verifies the given {@code address}.
	 *
	 * @param address
	 *            the address to be verified.
	 * @throws FASTUnknownAddressException
	 *             if the given {@code address} is not associated with this
	 *             node.
	 */
	public void verifyAddress( byte [] address );

}
