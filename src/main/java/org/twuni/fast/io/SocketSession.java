package org.twuni.fast.io;

import java.io.IOException;
import java.net.Socket;

import org.twuni.fast.EventHandler;
import org.twuni.fast.Session;

/**
 * Convenience wrapper for constructing a session from a {@link Socket}.
 */
public class SocketSession extends Session {

	public SocketSession( Socket socket ) throws IOException {
		this( socket, null );
	}

	public SocketSession( Socket socket, EventHandler eventHandler ) throws IOException {
		super( socket.getInputStream(), socket.getOutputStream(), eventHandler );
	}

}
