package org.twuni.fast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A stateful object is capable of saving and restoring its state.
 */
public interface Stateful extends FAST {

	/**
	 * Restores this object's state from the given {@code input} stream.
	 *
	 * @param input
	 *            the stream from which to read the object's state.
	 * @throws IOException
	 *             if the stream does not contain a valid state, or if a
	 *             communications error occurs while attempting to read the
	 *             state.
	 */
	public void restoreState( InputStream input ) throws IOException;

	/**
	 * Saves this object's state to the given {@code output} stream.
	 *
	 * @param output
	 *            the stream to which the object's state should be written.
	 * @throws IOException
	 *             if a communications error occurs while attempting to write
	 *             the state.
	 */
	public void saveState( OutputStream output ) throws IOException;

}
