package org.twuni.fast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.twuni.fast.io.ReadChannel;
import org.twuni.fast.io.WriteChannel;
import org.twuni.fast.model.Packet;

public class Client {

	public static class Builder {

		private String host;
		private String user;
		private String password;
		private int port;
		private boolean secure;
		private PacketListener packetListener;

		public Builder() {
			reset();
		}

		public Client build() throws UnknownHostException, IOException {
			return new Client( secure, host, port, user, password, packetListener );
		}

		public Builder host( String host ) {
			this.host = host;
			return this;
		}

		public Builder packetListener( PacketListener packetListener ) {
			this.packetListener = packetListener;
			return this;
		}

		public Builder password( String password ) {
			this.password = password;
			return this;
		}

		public Builder port( int port ) {
			this.port = port;
			return this;
		}

		public Builder reset() {
			host = null;
			user = null;
			password = null;
			port = 4857;
			secure = true;
			return this;
		}

		public Builder secure( boolean secure ) {
			this.secure = secure;
			return this;
		}

		public Builder user( String user ) {
			this.user = user;
			return this;
		}

	}

	private final WriteChannel w;
	private final ReadChannel r;

	protected Client( boolean secure, String host, int port, String user, String password, PacketListener packetListener ) throws UnknownHostException, IOException {
		byte [] credential = String.format( "%s\n%s", user, password ).getBytes();
		Socket socket = secure ? SSLSocketFactory.getDefault().createSocket( host, port ) : SocketFactory.getDefault().createSocket( host, port );
		w = new WriteChannel( socket.getOutputStream() );
		EventHandler e = new EventHandlers( new ReliableEventHandler( w ), new Authenticator( credential, w ), new PacketListenerWrapper( packetListener ) );
		r = new ReadChannel( socket.getInputStream(), e );
		w.setEventHandler( e );
		r.loopInBackground();
		w.connect().attach( host );
	}

	public void close() {
		w.detach();
		r.disconnect();
	}

	public void send( Packet... packets ) {
		w.send( packets );
	}

}
