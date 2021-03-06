package org.twuni.fast.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.twuni.fast.FAST;

public class IOUtils implements FAST {

	public static byte [] burn( byte [] array ) {
		Arrays.fill( array, (byte) 0 );
		return array;
	}

	public static ByteBuffer burn( ByteBuffer buffer ) {
		buffer.flip();
		int length = buffer.limit();
		byte zero = 0;
		for( int i = 0; i < length; i++ ) {
			buffer.put( i, zero );
		}
		return buffer;
	}

	public static char [] burn( char [] array ) {
		Arrays.fill( array, (char) 0 );
		return array;
	}

	public static CharBuffer burn( CharBuffer buffer ) {
		buffer.flip();
		int length = buffer.limit();
		char zero = 0;
		for( int i = 0; i < length; i++ ) {
			buffer.put( i, zero );
		}
		return buffer;
	}

	public static int pipe( InputStream input, OutputStream output ) throws IOException {
		return pipe( input, output, DEFAULT_BUFFER_SIZE );
	}

	public static int pipe( InputStream input, OutputStream output, byte [] buffer ) throws IOException {
		return pipe( input, output, buffer, 0, buffer.length );
	}

	public static int pipe( InputStream input, OutputStream output, byte [] buffer, int offset, int length ) throws IOException {
		int bytesWritten = 0;
		for( int size = input.read( buffer, offset, length ); size > -1; size = input.read( buffer, offset, length ) ) {
			output.write( buffer, offset, size );
			bytesWritten += size;
		}
		return bytesWritten;
	}

	public static int pipe( InputStream input, OutputStream output, int bufferSize ) throws IOException {
		return pipe( input, output, new byte [bufferSize] );
	}

	public static byte [] readBuffer( InputStream in ) throws IOException {
		int length = readInt( in );
		byte [] buffer = new byte [length];
		IOUtils.readFully( in, buffer );
		return buffer;
	}

	public static byte [] readFully( InputStream in ) throws IOException {
		return readFully( in, DEFAULT_BUFFER_SIZE );
	}

	public static byte [] readFully( InputStream in, byte [] buffer ) throws IOException {
		return readFully( in, buffer, 0, buffer != null ? buffer.length : 0 );
	}

	public static byte [] readFully( InputStream in, byte [] buffer, int offset, int length ) throws IOException {
		int bytesRead = 0;
		while( bytesRead < length ) {
			int bytesReadThisTime = in.read( buffer, offset + bytesRead, length - bytesRead );
			if( bytesReadThisTime > 0 ) {
				bytesRead += bytesReadThisTime;
			} else {
				throw new EOFException();
			}
		}
		return buffer;
	}

	public static byte [] readFully( InputStream in, int bufferSize ) throws IOException {
		return readFully( in, new byte [bufferSize] );
	}

	public static int readInt( InputStream in ) throws IOException {
		byte [] buffer = new byte [4];
		readFully( in, buffer );
		return toInt( buffer );
	}

	public static long readLong( InputStream in ) throws IOException {
		byte [] buffer = new byte [8];
		readFully( in, buffer );
		return toLong( buffer );
	}

	public static byte [] readSmallBuffer( InputStream in ) throws IOException {
		int length = in.read();
		byte [] buffer = new byte [length];
		IOUtils.readFully( in, buffer );
		return buffer;
	}

	public static byte [] toByteArray( ByteBuffer buffer ) {
		byte [] array = new byte [buffer.limit()];
		buffer.get( array );
		return array;
	}

	public static byte [] toByteArray( CharBuffer buffer ) {
		ByteBuffer bb = UTF8.encode( buffer );
		byte [] array = toByteArray( bb );
		burn( bb );
		return array;
	}

	public static byte [] toByteArray( CharSequence string ) {
		CharBuffer cb = CharBuffer.wrap( string );
		return toByteArray( cb );
	}

	public static byte [] toByteArray( int n ) {
		return new byte [] { (byte) ( 0xFF & n >> 24 ), (byte) ( 0xFF & n >> 16 ), (byte) ( 0xFF & n >> 8 ), (byte) ( 0xFF & n >> 0 ) };
	}

	public static byte [] toByteArray( long n ) {
		return new byte [] { (byte) ( 0xFF & n >> 56 ), (byte) ( 0xFF & n >> 48 ), (byte) ( 0xFF & n >> 40 ), (byte) ( 0xFF & n >> 32 ), (byte) ( 0xFF & n >> 24 ), (byte) ( 0xFF & n >> 16 ), (byte) ( 0xFF & n >> 8 ), (byte) ( 0xFF & n >> 0 ) };
	}

	public static int toInt( byte [] buffer ) {
		return toInt( buffer, 0 );
	}

	public static int toInt( byte [] buffer, int offset ) {
		int a = 0xFF000000 & buffer[offset + 0] << 24;
		int b = 0x00FF0000 & buffer[offset + 1] << 16;
		int c = 0x0000FF00 & buffer[offset + 2] << 8;
		int d = 0x000000FF & buffer[offset + 3] << 0;
		return a | b | c | d;
	}

	public static long toLong( byte [] buffer ) {
		return toLong( buffer, 0 );
	}

	public static long toLong( byte [] buffer, int offset ) {
		long a = 0xFF00000000000000L & (long) buffer[offset + 0] << 56;
		long b = 0x00FF000000000000L & (long) buffer[offset + 1] << 48;
		long c = 0x0000FF0000000000L & (long) buffer[offset + 2] << 40;
		long d = 0x000000FF00000000L & (long) buffer[offset + 3] << 32;
		long e = 0x00000000FF000000L & (long) buffer[offset + 4] << 24;
		long f = 0x0000000000FF0000L & (long) buffer[offset + 5] << 16;
		long g = 0x000000000000FF00L & (long) buffer[offset + 6] << 8;
		long h = 0x00000000000000FFL & (long) buffer[offset + 7] << 0;
		return a | b | c | d | e | f | g | h;
	}

	public static void writeBuffer( OutputStream out, byte [] buffer ) throws IOException {
		writeBuffer( out, buffer, 0, buffer != null ? buffer.length : 0 );
	}

	public static void writeBuffer( OutputStream out, byte [] buffer, int offset, int length ) throws IOException {
		out.write( toByteArray( length ) );
		if( buffer != null ) {
			out.write( buffer, offset, length );
		}
	}

	public static void writeBuffer( OutputStream out, String value ) throws IOException {
		writeBuffer( out, value.getBytes() );
	}

	public static void writeInt( OutputStream out, int value ) throws IOException {
		out.write( IOUtils.toByteArray( value ) );
	}

	public static void writeLong( OutputStream out, long value ) throws IOException {
		out.write( IOUtils.toByteArray( value ) );
	}

	public static void writeSmallBuffer( OutputStream out, byte [] buffer ) throws IOException {
		writeSmallBuffer( out, buffer, 0, buffer != null ? buffer.length : 0 );
	}

	public static void writeSmallBuffer( OutputStream out, byte [] buffer, int offset, int length ) throws IOException {
		out.write( length );
		if( buffer != null ) {
			out.write( buffer, offset, length );
		}
	}

	public static void writeSmallBuffer( OutputStream out, String value ) throws IOException {
		writeSmallBuffer( out, value.getBytes() );
	}

	private static final Charset UTF8 = Charset.forName( "UTF-8" );

	private static final int DEFAULT_BUFFER_SIZE = 1 * 1024;

	private IOUtils() {
		// Prevent instances of this class from being constructed.
	}

}
