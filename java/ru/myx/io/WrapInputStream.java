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
public final class WrapInputStream extends InputStream {
	private final byte[]	bytes;
	
	private int				position;
	
	private final int		limit;
	
	private int				mark	= -1;
	
	/**
	 * @param bytes
	 * @param position
	 * @param length
	 */
	public WrapInputStream(final byte[] bytes, final int position, final int length) {
		this.bytes = bytes;
		this.position = position;
		this.limit = length + position;
	}
	
	@Override
	public int available() {
		return this.limit - this.position;
	}
	
	@Override
	public void close() {
		// ignore
	}
	
	@Override
	public void mark(final int readlimit) {
		this.mark = this.position;
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public int read() {
		return this.position == this.limit
				? -1
				: this.bytes[this.position++] & 0xFF;
	}
	
	@Override
	public int read(final byte[] b) throws IOException {
		return this.read( b, 0, b.length );
	}
	
	@Override
	public int read(final byte[] b, final int off, final int len) {
		if (this.position == this.limit) {
			return -1;
		}
		final int read = Math.min( len, this.limit - this.position );
		System.arraycopy( this.bytes, this.position, b, off, read );
		this.position += read;
		return read;
	}
	
	@Override
	public void reset() throws IOException {
		if (this.mark == -1) {
			throw new IOException( "mark not set!" );
		}
		this.position = this.mark;
	}
	
	@Override
	public long skip(final long n) {
		final int skip = Math.min( (int) n, this.limit - this.position );
		this.position += skip;
		return skip;
	}
}
