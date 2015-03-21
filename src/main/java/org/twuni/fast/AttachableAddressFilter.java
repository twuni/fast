package org.twuni.fast;

/**
 * This filter causes incoming ATTACH commands to be rejected if the address
 * associated with the command does not belong to this node.
 */
public class AttachableAddressFilter extends EventHandlerBase {

	private final AddressVerifier addressVerifier;

	/**
	 * Initializes this filter to verify the address given with ATTACH commands
	 * against the given {@code addressVerifier}.
	 *
	 * @param addressVerifier
	 *            the verifier for determining whether an address is attachable.
	 */
	public AttachableAddressFilter( AddressVerifier addressVerifier ) {
		this.addressVerifier = addressVerifier;
	}

	@Override
	public void onAttachRequested( byte [] address ) {
		addressVerifier.verifyAddress( address );
	}

}
