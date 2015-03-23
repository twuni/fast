package org.twuni.fast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.twuni.fast.io.ReadChannel;
import org.twuni.fast.io.WriteChannel;

/**
 * A high-level implementation of a FAST server node capable of accepting
 * connections from many client nodes.
 */
public class Server {

	/**
	 * Builds a new {@link Server} instance.
	 */
	public static class Builder {

		private int port;
		private boolean secure;
		private PacketListener packetListener;
		private AddressVerifier addressVerifier;
		private SessionFactory sessionFactory;
		private Authenticator authenticator;
		private MailboxFactory mailboxFactory;
		private PacketRouter packetRouter;
		private PrintStream logger;

		/**
		 * Initializes a new builder in its default state.
		 */
		public Builder() {
			reset();
		}

		/**
		 * Configures the server to use the given {@code addressVerifier} for
		 * determining whether an address is a valid local target to which a
		 * client can request attachment.
		 *
		 * @param addressVerifier
		 *            the verifier the server should use when determining
		 *            whether an address is a valid local target to which a
		 *            client can request attachment.
		 * @return this object, for chaining commands.
		 */
		public Builder addressVerifier( AddressVerifier addressVerifier ) {
			this.addressVerifier = addressVerifier;
			return this;
		}

		/**
		 * Configures the server to use the given {@code authenticator} for
		 * authenticating client nodes.
		 *
		 * @param authenticator
		 *            the authenticator to use for authenticating client nodes.
		 * @return this object, for chaining commands.
		 */
		public Builder authenticator( Authenticator authenticator ) {
			this.authenticator = authenticator;
			return this;
		}

		public Server build() {
			return new Server( secure, port, packetListener, addressVerifier, sessionFactory, authenticator, mailboxFactory, packetRouter, logger );
		}

		/**
		 * Configures the server to log to the given {@code logger}.
		 *
		 * @param logger
		 *            the logger to which the server should record its logs.
		 * @return this object, for chaining commands.
		 */
		public Builder logger( PrintStream logger ) {
			this.logger = logger;
			return this;
		}

		/**
		 * Configures the server to use the given {@code mailboxFactory} when
		 * fetching new packets to deliver to a client node.
		 *
		 * @param mailboxFactory
		 *            the factory to use to obtain a {@link Mailbox} to
		 *            consult when fetching new packets to deliver to a client
		 *            node.
		 * @return this object, for chaining commands.
		 */
		public Builder mailboxFactory( MailboxFactory mailboxFactory ) {
			this.mailboxFactory = mailboxFactory;
			return this;
		}

		/**
		 * Configures the server to notify the given {@code packetListener}
		 * whenever packets are sent and received.
		 *
		 * @param packetListener
		 *            the listener to be notified of incoming and outgoing
		 *            packets.
		 * @return this object, for chaining commands.
		 */
		public Builder packetListener( PacketListener packetListener ) {
			this.packetListener = packetListener;
			return this;
		}

		/**
		 * Configures the server to use the given {@code packetRouter} when
		 * attempting to deliver packets to an address.
		 *
		 * @param packetRouter
		 *            the router to use for delivering packets.
		 * @return this object, for chaining commands.
		 */
		public Builder packetRouter( PacketRouter packetRouter ) {
			this.packetRouter = packetRouter;
			return this;
		}

		/**
		 * Configures the server to listen on the given TCP {@code port}.
		 *
		 * @param port
		 *            the TCP port on which the server should listen.
		 * @return this object, for chaining commands.
		 */
		public Builder port( int port ) {
			this.port = port;
			return this;
		}

		/**
		 * Resets this builder to its default, uninitialized state.
		 *
		 * @return this object, for chaining commands.
		 */
		public Builder reset() {
			port = 4857;
			secure = true;
			packetListener = null;
			addressVerifier = null;
			sessionFactory = null;
			authenticator = null;
			logger = null;
			mailboxFactory = null;
			packetRouter = null;
			return this;
		}

		/**
		 * Configures whether this server should listen on a secure (TLS) socket
		 * or an insecure socket.
		 *
		 * @param secure
		 *            {@code true} if the server should listen on a secure (TLS)
		 *            socket, or {@code false} to listen on an insecure socket.
		 * @return this object, for chaining commands.
		 */
		public Builder secure( boolean secure ) {
			this.secure = secure;
			return this;
		}

		/**
		 * Configures the server to use the given {@code sessionFactory} when
		 * creating a session for a client node.
		 *
		 * @param sessionFactory
		 *            the factory to use for constructing a session for a client
		 *            node.
		 * @return this object, for chaining commands.
		 */
		public Builder sessionFactory( SessionFactory sessionFactory ) {
			this.sessionFactory = sessionFactory;
			return this;
		}

	}

	static class Looper implements Runnable {

		private final boolean secure;
		private final int port;
		private final PacketListener packetListener;
		private final AddressVerifier addressVerifier;
		private final SessionFactory sessionFactory;
		private final Authenticator authenticator;
		private final MailboxFactory mailboxFactory;
		private final PacketRouter packetRouter;
		private final WriteChannelProvider writeChannelProvider;
		private final PrintStream logger;

		public Looper( boolean secure, int port, PacketListener packetListener, AddressVerifier addressVerifier, SessionFactory sessionFactory, Authenticator authenticator, MailboxFactory mailboxFactory, PacketRouter packetRouter, WriteChannelProvider writeChannelProvider, PrintStream logger ) {
			this.secure = secure;
			this.port = port;
			this.packetListener = packetListener;
			this.addressVerifier = addressVerifier;
			this.sessionFactory = sessionFactory;
			this.authenticator = authenticator;
			this.mailboxFactory = mailboxFactory;
			this.packetRouter = packetRouter;
			this.writeChannelProvider = writeChannelProvider;
			this.logger = logger;
		}

		@Override
		public void run() {
			try {
				ServerSocket server = secure ? SSLServerSocketFactory.getDefault().createServerSocket( port ) : ServerSocketFactory.getDefault().createServerSocket( port );
				try {
					while( !Thread.interrupted() ) {
						Socket socket = server.accept();
						WriteChannel w = new WriteChannel( socket.getOutputStream() );
						EventHandler e = new ServerEventHandler( w, packetListener, addressVerifier, sessionFactory, authenticator, mailboxFactory, packetRouter, writeChannelProvider, logger );
						w.setEventHandler( e );
						ReadChannel r = new ReadChannel( socket.getInputStream(), e );
						r.accept().loopInBackground();
					}
				} finally {
					server.close();
				}
			} catch( IOException exception ) {
				new ClojureEventLogger( logger ).onException( exception );
			}
		}

	}

	public static void main( String [] args ) {

		Builder b = new Builder();

		b.logger( System.out );

		int port = 4857;
		boolean secure = false;
		byte [] realm = null;

		for( int i = 0; i < args.length; i++ ) {

			if( "-?".equals( args[i] ) ) {
				printUsage();
				return;
			}

			if( "-p".equals( args[i] ) ) {
				i++;
				port = Integer.parseInt( args[i] );
				continue;
			}

			if( "-k".equals( args[i] ) ) {
				secure = false;
				continue;
			}

			if( "-s".equals( args[i] ) ) {
				secure = true;
				continue;
			}

			if( "-r".equals( args[i] ) ) {
				i++;
				realm = args[i].getBytes();
				continue;
			}

		}

		if( realm != null ) {
			b.addressVerifier( new WhiteListAddressFilter( realm ) );
		}

		Server s = b.port( port ).secure( secure ).build();

		if( realm != null ) {
			System.out.println( String.format( "(listen :realm \"%s\" :port %d :secure %b)", new String( realm ), Integer.valueOf( port ), Boolean.valueOf( secure ) ) );
		} else {
			System.out.println( String.format( "(listen :port %d :secure %b)", Integer.valueOf( port ), Boolean.valueOf( secure ) ) );
		}

		s.startListening();

		try {
			while( !Thread.interrupted() ) {
				Thread.sleep( Long.MAX_VALUE );
			}
		} catch( InterruptedException exception ) {
			s.stopListening();
		}

	}

	private static void printUsage() {
		System.out.println( "Usage: fast-server OPTIONS" );
		System.out.println( "OPTIONS:" );
		System.out.println( "    -?         Print this help message." );
		System.out.println( "    -p <port>  Default: 4857" );
		System.out.println( "    -k         Listen on an insecure socket (default)." );
		System.out.println( "    -s         Listen on a TLS socket." );
		System.out.println( "    -r <realm> Listen on the given realm. Default: (any)" );
	}

	private final boolean secure;
	private final int port;
	private final PacketListener packetListener;
	private final AddressVerifier addressVerifier;
	private final SessionFactory sessionFactory;
	private final Authenticator authenticator;
	private final MailboxFactory mailboxFactory;
	private final PacketRouter packetRouter;
	private final PrintStream logger;
	private final WriteChannelProvider writeChannelProvider;

	private Thread listenerThread;

	/**
	 * Initializes a new server node with the given configuration parameters.
	 *
	 * @param secure
	 *            {@code true} if the server should listen on a secure (TLS)
	 *            socket, or {@code false} to listen on an insecure socket.
	 * @param port
	 *            the TCP port on which the server should listen.
	 * @param packetListener
	 *            the listener to be notified of incoming and outgoing
	 *            packets.
	 * @param addressVerifier
	 *            the verifier the server should use when determining
	 *            whether an address is a valid local target to which a
	 *            client can request attachment.
	 * @param sessionFactory
	 *            the factory to use for constructing a session for a client
	 *            node.
	 * @param authenticator
	 *            the authenticator to use for authenticating client nodes.
	 * @param mailboxFactory
	 *            the factory to use to obtain a {@link Mailbox} to
	 *            consult when fetching new packets to deliver to a client
	 *            node.
	 * @param packetRouter
	 *            the router to use for delivering packets.
	 * @param logger
	 *            the logger to which the server should record its logs.
	 */
	protected Server( boolean secure, int port, PacketListener packetListener, AddressVerifier addressVerifier, SessionFactory sessionFactory, Authenticator authenticator, MailboxFactory mailboxFactory, PacketRouter packetRouter, PrintStream logger ) {
		this.secure = secure;
		this.port = port;
		this.packetListener = packetListener != null ? packetListener : new EventHandlerBase();
		this.addressVerifier = addressVerifier != null ? addressVerifier : new AnyAddressFilter();
		this.sessionFactory = sessionFactory != null ? sessionFactory : new AnonymousSessionFactory();
		this.authenticator = authenticator != null ? authenticator : new AutomaticAuthenticator();
		writeChannelProvider = new SimpleWriteChannelProvider();
		if( mailboxFactory == null || packetRouter == null ) {
			InternalPacketTransport transport = new InternalPacketTransport( writeChannelProvider );
			this.mailboxFactory = transport;
			this.packetRouter = transport;
		} else {
			this.mailboxFactory = mailboxFactory;
			this.packetRouter = packetRouter;
		}
		this.logger = logger;
	}

	/**
	 * Returns whether this server is currently accepting incoming connections.
	 *
	 * @return {@code true} if this server is currently accepting incoming
	 *         connections. Otherwise returns {@code false}.
	 */
	public boolean isListening() {
		return listenerThread != null && listenerThread.isAlive();
	}

	/**
	 * Notifies this server to immediately start accepting incoming connections
	 * in a background thread. If this server is already listening for
	 * connections, this method does nothing.
	 *
	 * @see #isListening()
	 */
	public void startListening() {
		if( isListening() ) {
			return;
		}
		Looper looper = new Looper( secure, port, packetListener, addressVerifier, sessionFactory, authenticator, mailboxFactory, packetRouter, writeChannelProvider, logger );
		listenerThread = new Thread( looper, String.format( "%s(%s)", Looper.class.getName(), Integer.toHexString( hashCode() ) ) );
		listenerThread.start();
	}

	/**
	 * Notifies this server to stop accepting incoming connections, and to
	 * disconnect any currently connected clients.
	 */
	public void stopListening() {
		if( !isListening() ) {
			return;
		}
		listenerThread.interrupt();
		listenerThread = null;
	}

}
