package org.twuni.fast;

import java.util.Arrays;

import org.twuni.fast.exception.FASTUnknownAddressException;

/**
 * An address verifier that accepts only addresses in a predefined white list.
 */
public class WhiteListAddressFilter implements AddressVerifier {

	private final byte [][] acceptableAddresses;

	public WhiteListAddressFilter( byte []... acceptableAddresses ) {
		this.acceptableAddresses = acceptableAddresses;
	}

	@Override
	public void verifyAddress( byte [] address ) {
		for( byte [] acceptableAddress : acceptableAddresses ) {
			if( Arrays.equals( address, acceptableAddress ) ) {
				return;
			}
		}
		throw new FASTUnknownAddressException();
	}

}
