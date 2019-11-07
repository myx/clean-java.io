package ru.myx.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * It doesn't copy bytes to a new array, just wraps them.
 * 
 * If you can't tell the difference then you shouldn't use this class.
 * 
 * It does support mark/reset.
 * 
 * @author myx
 * 
 */
public final class SingletonInputStream extends InputStream {
	private int	theByte;
	
	private int	theMark	= -1;
	
	/**
	 * 
	 * @param oneByte
	 */
	public SingletonInputStream(final byte oneByte) {
		this.theByte = oneByte & 0xFF;
	}
	
	@Override
	public int available() {
		return this.theByte == -1
				? 0
				: 1;
	}
	
	@Override
	public void close() {
		// ignore
	}
	
	@Override
	public void mark(final int readlimit) {
		this.theMark = this.theByte;
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public int read() {
		return this.theByte == -1
				? -1
				: this.theByte;
	}
	
	@Override
	public int read(final byte[] b) throws IOException {
		return this.read( b, 0, b.length );
	}
	
	@Override
	public int read(final byte[] b, final int off, final int len) {
		if (this.theByte == -1) {
			return -1;
		}
		if (len == 0) {
			return 0;
		}
		b[off] = (byte) this.theByte;
		this.theByte = -1;
		return 1;
	}
	
	@Override
	public void reset() throws IOException {
		if (this.theMark == -1) {
			throw new IOException( "mark not set!" );
		}
		this.theByte = this.theMark;
	}
	
	@Override
	public long skip(final long n) {
		if (this.theByte == -1 || n == 0) {
			return 0;
		}
		this.theByte = -1;
		return 1;
	}
}
