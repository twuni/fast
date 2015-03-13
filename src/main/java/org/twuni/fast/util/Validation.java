package org.twuni.fast.util;

import org.twuni.fast.FAST;

public class Validation implements FAST {

	public static void assertBetween( int minimum, int value, int maximum, String label ) {
		if( value < minimum || maximum < value ) {
			throw new IllegalArgumentException( String.format( "%s must be between %d and %d.", label, Integer.valueOf( minimum ), Integer.valueOf( maximum ) ) );
		}
	}

	public static void assertLengthBetween( int minimum, byte [] value, int maximum, String label ) {
		assertBetween( minimum, value != null ? value.length : 0, maximum, String.format( "%s length", label ) );
	}

	private Validation() {
		// Prevent instances of this class from being constructed.
	}

}
