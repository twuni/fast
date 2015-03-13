package org.twuni.fast;

/**
 * This interface simply serves to indicate that a particular class is
 * related to FAST in some way.
 */
public interface FAST {

	/**
	 * The constant header sent by FAST clients when attempting to establish a
	 * session.
	 */
	public static final byte [] FAST_HEADER = { 'F', 'A', 'S', 'T', 1, 0 };

}
