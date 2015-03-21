package org.twuni.fast;

/**
 * An address verifier that accepts all addresses.
 */
public class AnyAddressFilter implements AddressVerifier {

	@Override
	public void verifyAddress( byte [] address ) {
		// Accept all addresses.
	}

}
