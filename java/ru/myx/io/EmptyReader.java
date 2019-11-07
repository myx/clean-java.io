package ru.myx.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * 
 * An empty java.io.InputStream and java.lang.Readable implementation.
 * 
 * @author myx
 * 
 */
public final class EmptyReader extends Reader {
	/**
	 * Singular instance of an empty java.io.InputStream and java.lang.Readable
	 * interfaces.
	 */
	public static final EmptyReader	INSTANCE	= new EmptyReader();
	
	private EmptyReader() {
		// prevent
	}
	
	@Override
	public void close() {
		// ignore
	}
	
	@Override
	public void mark(final int readlimit) {
		// ignore
	}
	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public int read() {
		return -1;
	}
	
	@Override
	public int read(final char[] b) {
		return -1;
	}
	
	@Override
	public int read(final char[] b, final int off, final int len) {
		return -1;
	}
	
	@Override
	public int read(final CharBuffer cb) throws IOException {
		return -1;
	}
	
	@Override
	public void reset() {
		// ignore
	}
	
	@Override
	public long skip(final long n) {
		return 0;
	}
	
}
