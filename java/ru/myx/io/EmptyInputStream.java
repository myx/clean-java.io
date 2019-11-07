package ru.myx.io;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;

/**
 * 
 * An empty java.io.InputStream and java.lang.Readable implementation.
 * 
 * @author myx
 * 
 */
public final class EmptyInputStream extends InputStream implements DataInput, Readable {
	/**
	 * Singular instance of an empty java.io.InputStream and java.lang.Readable
	 * interfaces.
	 */
	public static final EmptyInputStream	INSTANCE	= new EmptyInputStream();
	
	private EmptyInputStream() {
		// prevent
	}
	
	@Override
	public int available() {
		return 0;
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
	public int read(final byte[] b) {
		return -1;
	}
	
	@Override
	public int read(final byte[] b, final int off, final int len) {
		return -1;
	}
	
	@Override
	public int read(final CharBuffer cb) throws IOException {
		return -1;
	}
	
	@Override
	public boolean readBoolean() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public byte readByte() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public char readChar() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public double readDouble() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public float readFloat() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public void readFully(final byte[] b) throws IOException {
		throw new EOFException();
	}
	
	@Override
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		throw new EOFException();
	}
	
	@Override
	public int readInt() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public String readLine() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public long readLong() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public short readShort() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public int readUnsignedByte() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public int readUnsignedShort() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public String readUTF() throws IOException {
		throw new EOFException();
	}
	
	@Override
	public void reset() {
		// ignore
	}
	
	@Override
	public long skip(final long n) {
		return 0;
	}
	
	@Override
	public int skipBytes(final int n) throws IOException {
		throw new EOFException();
	}
	
}
