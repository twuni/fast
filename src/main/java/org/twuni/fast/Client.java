package org.twuni.fast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.twuni.fast.io.ReadChannel;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;
import org.twuni.fast.util.IOUtils;

/**
 * A high-level implementation of a FAST client node capable of connecting to a
 * remote server node.
 */
public class Client {

	/**
	 * Builds a new {@link Client} instance.
	 */
	public static class Builder {

		private String host;
		private byte [] credential;
		private int port;
		private boolean secure;
		private PacketListener packetListener;

		/**
		 * Initializes a new builder in its default state.
		 */
		public Builder() {
			reset();
		}

		/**
		 * Creates a new {@link Client} instance initialized from this builder's
		 * configuration.
		 *
		 * @return the newly constructed client.
		 * @throws UnknownHostException
		 *             if the configured host cannot be mapped to an Internet
		 *             address.
		 * @throws IOException
		 *             if a network error occurs while initializing the client.
		 */
		public Client build() throws UnknownHostException, IOException {
			return new Client( secure, host, port, credential, packetListener );
		}

		/**
		 * Use the given {@code credential} for authentication.
		 *
		 * @param credential
		 *            the credential to use for authentication.
		 * @return this object, for chaining commands.
		 */
		public Builder credential( byte [] credential ) {
			this.credential = credential;
			return this;
		}

		/**
		 * Use the given {@code user} and {@code password} for basic
		 * authentication, which concatenates the two arrays with a newline
		 * delimiter in between.
		 *
		 * @param username
		 *            the username component of the credential.
		 * @param password
		 *            the password component of the credential.
		 * @return this object, for chaining commands.
		 */
		public Builder credential( byte [] username, byte [] password ) {
			byte [] credential = new byte [username.length + password.length + 1];
			System.arraycopy( username, 0, credential, 0, username.length );
			credential[username.length] = '\n';
			System.arraycopy( password, 0, credential, username.length + 1, password.length );
			return credential( credential );
		}

		/**
		 * Use the given {@code user} and {@code password} for basic
		 * authentication, which concatenates the two arrays with a newline
		 * delimiter in between.
		 *
		 * @param username
		 *            the username component of the credential.
		 * @param password
		 *            the password component of the credential.
		 * @return this object, for chaining commands.
		 */
		public Builder credential( CharSequence username, CharSequence password ) {
			byte [] usernameBytes = IOUtils.toByteArray( username );
			byte [] passwordBytes = IOUtils.toByteArray( password );
			try {
				return credential( usernameBytes, passwordBytes );
			} finally {
				IOUtils.burn( usernameBytes );
				IOUtils.burn( passwordBytes );
			}
		}

		/**
		 * Configures the client to connect to the given {@code host}.
		 *
		 * @param host
		 *            the host to which the client should connect.
		 * @return this object, for chaining commands.
		 */
		public Builder host( String host ) {
			this.host = host;
			return this;
		}

		/**
		 * Notify the given {@code packetListener} whenever this client sends
		 * and receives packets.
		 *
		 * @param packetListener
		 *            the listener to be notified.
		 * @return this object, for chaining commands.
		 */
		public Builder packetListener( PacketListener packetListener ) {
			this.packetListener = packetListener;
			return this;
		}

		/**
		 * Configures the client to connect on the given {@code port}.
		 *
		 * @param port
		 *            the port to which the client should connect.
		 * @return this object, for chaining commands.
		 */
		public Builder port( int port ) {
			this.port = port;
			return this;
		}

		/**
		 * Resets this builder to its uninitialized default state.
		 *
		 * @return this object, for chaining commands.
		 */
		public Builder reset() {
			host = null;
			port = 4857;
			credential = null;
			secure = true;
			packetListener = null;
			return this;
		}

		/**
		 * Configures whether the client should use a secure (TLS) socket when
		 * connecting to the remote node.
		 *
		 * @param secure
		 *            {@code true} if the client should use a secure (TLS)
		 *            socket, or {@code false} if the client should use an
		 *            insecure socket.
		 * @return this object, for chaining commands.
		 */
		public Builder secure( boolean secure ) {
			this.secure = secure;
			return this;
		}

	}

	private final WriteChannel w;
	private final ReadChannel r;
	private final ClientEventHandler e;

	/**
	 * Initializes a client to connect to the given {@code host} and
	 * {@code port}, using the given {@code credential} for authentication, and
	 * notifying the given {@code packetListener} of incoming and outgoing
	 * packets.
	 *
	 * @param secure
	 *            {@code true} if the client should connect via a secure (TLS)
	 *            socket, or {@code false} if the client should connect via an
	 *            insecure socket.
	 * @param host
	 *            the hostname of the remote node to which this client will
	 *            connect.
	 * @param port
	 *            the port on the remote node to which this client will connect.
	 * @param credential
	 *            the credential to use for authentication to the remote node.
	 * @param packetListener
	 *            the listener to be notified of sent and received packets.
	 * @throws UnknownHostException
	 *             if the given {@code host} cannot be mapped to an Internet
	 *             address.
	 * @throws IOException
	 *             if a network error occurs.
	 */
	protected Client( boolean secure, String host, int port, byte [] credential, PacketListener packetListener ) throws UnknownHostException, IOException {
		Socket socket = secure ? SSLSocketFactory.getDefault().createSocket( host, port ) : SocketFactory.getDefault().createSocket( host, port );
		w = new WriteChannel( socket.getOutputStream() );
		e = new ClientEventHandler( w, credential, packetListener );
		r = new ReadChannel( socket.getInputStream(), e );
		w.setEventHandler( e );
		r.loopInBackground();
		w.connect().attach( host );
	}

	/**
	 * Closes the client session.
	 */
	public void close() {
		w.detach();
		r.disconnect();
	}

	/**
	 * Returns the local address assigned by the remote node after
	 * authentication.
	 *
	 * @return the local address assigned by the remote node after
	 *         authentication.
	 */
	public byte [] getIdentity() {
		return e.getIdentity();
	}

	/**
	 * Returns the identity assigned by the remote node for this session after
	 * attaching.
	 *
	 * @return the identity assigned by the remote node for this session after
	 *         attaching.
	 */
	public byte [] getSessionID() {
		return e.getSessionID();
	}

	/**
	 * Sends an acknowledgment request to the remote node.
	 */
	public void requestAcknowledgment() {
		w.requestAcknowledgment();
	}

	/**
	 * Sends the given {@code packets} to the remote node.
	 *
	 * @param packets
	 *            the packets to be sent.
	 */
	public void send( Packet... packets ) {
		w.send( packets );
	}

}
