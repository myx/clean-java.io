package ru.myx.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * @author myx
 * 
 */
public class ReaderInputStream extends InputStream {
	private final Reader			reader;
	
	private final CharsetEncoder	encoder;
	
	private final CharBuffer		charBuffer;
	
	private final ByteBuffer		byteBuffer;
	
	/**
	 * @param reader
	 * @param charset
	 */
	public ReaderInputStream(final Reader reader, final Charset charset) {
		assert reader != null : "NULL reader!";
		this.reader = reader;
		this.encoder = charset.newEncoder();
		this.charBuffer = CharBuffer.allocate( 256 );
		this.byteBuffer = ByteBuffer.allocate( 256 );
		this.charBuffer.flip();
		this.byteBuffer.flip();
	}
	
	@Override
	public int available() throws IOException {
		return this.byteBuffer.remaining();
	}
	
	@Override
	public void close() throws IOException {
		this.reader.close();
		super.close();
	}
	
	@Override
	public void mark(final int readlimit) {
		throw new UnsupportedOperationException( "Unsupported!" );
	}
	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public int read() throws IOException {
		if (this.byteBuffer.hasRemaining()) {
			return this.byteBuffer.get() & 0xFF;
		}
		this.refill();
		return this.byteBuffer.hasRemaining()
				? this.byteBuffer.get() & 0xFF
				: -1;
	}
	
	@SuppressWarnings("unused")
	private boolean refill() throws IOException {
		System.out.println( ">>> moreBytes: " + this.byteBuffer.remaining() );
		if (!this.charBuffer.hasRemaining()) {
			this.charBuffer.clear();
			System.out.println( ">>> moreChars: " + this.charBuffer );
			this.reader.read( this.charBuffer );
			this.charBuffer.flip();
		}
		if (this.charBuffer.hasRemaining()) {
			this.byteBuffer.clear();
			System.out.println( ">>> encode: " + this.byteBuffer + ", " + this.charBuffer );
			final CoderResult result = this.encoder.encode( this.charBuffer, this.byteBuffer, false );
			System.out.println( ">>> encode result: " + result + ", " + this.byteBuffer + ", " + this.charBuffer );
			this.byteBuffer.flip();
		}
		/**
		 * fuck knows how this sun's stuff intended to work
		 */
		if (false && !this.byteBuffer.hasRemaining()) {
			this.byteBuffer.clear();
			System.out.println( ">>> flush: " + this.byteBuffer + ", " + this.charBuffer );
			final CoderResult result = this.encoder.flush( this.byteBuffer );
			System.out.println( ">>> flush result: " + result + ", " + this.byteBuffer + ", " + this.charBuffer );
			this.byteBuffer.flip();
		}
		return true;
	}
	
}
