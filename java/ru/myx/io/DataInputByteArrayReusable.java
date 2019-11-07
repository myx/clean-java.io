/**
 *
 */
package ru.myx.io;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/** Super fast, expensive, contains 16k buffer for UTF conversions, in any case faster than
 * ByteArrayInputStream and DataInputStream.
 *
 * BUFFER_UTF = 2x16k (1 char array)
 *
 * @author myx */
public final class DataInputByteArrayReusable extends InputStream implements DataInput {
	
	private final char[] chararr;
	
	private int count;
	
	private int pos;
	
	private int mark;
	
	private byte[] buffer;
	
	/**
	 *
	 */
	public DataInputByteArrayReusable() {

		this.chararr = new char[8 * 1024];
	}
	/** Creates an input stream. Not initialized, use setBytes method.
	 *
	 * @param charBufferCapacity
	 *            8 * 1024 .. 16 * 1024 */
	public DataInputByteArrayReusable(final int charBufferCapacity) {

		this.chararr = new char[charBufferCapacity];
	}
	
	@Override
	public int available() {

		return this.count - this.pos;
	}
	
	@Override
	public void mark(final int readLimit) {

		this.mark = this.pos;
	}
	
	@Override
	public boolean markSupported() {

		return true;
	}
	
	@Override
	public int read() {

		return this.pos < this.count
			? this.buffer[this.pos++] & 0xff
			: -1;
	}
	
	@Override
	public int read(final byte[] toBuf) {

		final int avail = this.count - this.pos;
		if (avail <= 0) {
			return -1;
		}
		final int amount = Math.min(toBuf.length, avail);
		System.arraycopy(this.buffer, this.pos, toBuf, 0, amount);
		this.pos += amount;
		return amount;
	}
	
	@Override
	public int read(final byte[] toBuf, final int offset, final int length) {

		final int avail = this.count - this.pos;
		if (avail <= 0) {
			return -1;
		}
		final int amount = length > avail
			? avail
			: length;
		System.arraycopy(this.buffer, this.pos, toBuf, offset, amount);
		this.pos += amount;
		return amount;
	}
	
	@Override
	public final boolean readBoolean() throws IOException {

		if (this.count - this.pos < 1) {
			throw new EOFException();
		}
		return this.buffer[this.pos++] != 0;
	}
	
	@Override
	public final byte readByte() throws IOException {

		if (this.count - this.pos < 1) {
			throw new EOFException();
		}
		return this.buffer[this.pos++];
	}
	
	@Override
	public final char readChar() throws IOException {

		if (this.count - this.pos < 2) {
			throw new EOFException();
		}
		return (char) (((this.buffer[this.pos++] & 0xff) << 8) + ((this.buffer[this.pos++] & 0xff) << 0));
	}
	
	@Override
	public final double readDouble() throws IOException {

		return Double.longBitsToDouble(this.readLong());
	}
	
	/** Equivalent to <code>read()<code> but does not throw
	 * <code>IOException</code>.
	 *
	 * @return
	 * @see #read() */
	public final int readFast() {

		return this.pos < this.count
			? this.buffer[this.pos++] & 0xff
			: -1;
	}
	
	/** Equivalent to <code>read(byte[])<code> but does not throw
	 * <code>IOException</code>.
	 *
	 * @param toBuf
	 * @return
	 * @see #read(byte[]) */
	public final int readFast(final byte[] toBuf) {

		final int avail = this.count - this.pos;
		if (avail <= 0) {
			return -1;
		}
		final int amount = Math.min(toBuf.length, avail);
		System.arraycopy(this.buffer, this.pos, toBuf, 0, amount);
		this.pos += amount;
		return amount;
	}
	
	/** Equivalent to <code>read(byte[],int,int)<code> but does not throw
	 * <code>IOException</code>.
	 *
	 * @param toBuf
	 * @param offset
	 * @param length
	 * @return
	 * @see #read(byte[],int,int) */
	public final int readFast(final byte[] toBuf, final int offset, final int length) {

		final int avail = this.count - this.pos;
		if (avail <= 0) {
			return -1;
		}
		final int amount = length > avail
			? avail
			: length;
		System.arraycopy(this.buffer, this.pos, toBuf, offset, amount);
		this.pos += amount;
		return amount;
	}
	
	@Override
	public final float readFloat() throws IOException {

		return Float.intBitsToFloat(this.readInt());
	}
	
	@Override
	public final void readFully(final byte b[]) throws IOException {

		final int len = b.length;
		if (this.count - this.pos < len) {
			throw new EOFException();
		}
		System.arraycopy(this.buffer, this.pos, b, 0, len);
		this.pos += len;
	}
	
	@Override
	public final void readFully(final byte b[], final int off, final int len) throws IOException {

		if (len < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (this.count - this.pos < len) {
			throw new EOFException();
		}
		System.arraycopy(this.buffer, this.pos, b, off, len);
		this.pos += len;
	}
	
	@Override
	public final int readInt() throws IOException {

		if (this.count - this.pos < 4) {
			throw new EOFException();
		}
		return ((this.buffer[this.pos++] & 0xff) << 24) + ((this.buffer[this.pos++] & 0xff) << 16) + ((this.buffer[this.pos++] & 0xff) << 8)
				+ ((this.buffer[this.pos++] & 0xff) << 0);
	}
	
	@Override
	@Deprecated
	public final String readLine() throws IOException {

		throw new UnsupportedOperationException("deprecated");
	}
	
	@Override
	public final long readLong() throws IOException {

		if (this.count - this.pos < 8) {
			throw new EOFException();
		}
		return ((long) (this.buffer[this.pos++] & 0xff) << 56) + ((long) (this.buffer[this.pos++] & 0xff) << 48) + ((long) (this.buffer[this.pos++] & 0xff) << 40)
				+ ((long) (this.buffer[this.pos++] & 0xff) << 32) + ((long) (this.buffer[this.pos++] & 0xff) << 24) + ((long) (this.buffer[this.pos++] & 0xff) << 16)
				+ ((long) (this.buffer[this.pos++] & 0xff) << 8) + (this.buffer[this.pos++] & 0xff);
	}
	
	@Override
	public final short readShort() throws IOException {

		if (this.count - this.pos < 2) {
			throw new EOFException();
		}
		return (short) (((this.buffer[this.pos++] & 0xff) << 8) + ((this.buffer[this.pos++] & 0xff) << 0));
	}
	
	@Override
	public final int readUnsignedByte() throws IOException {

		if (this.count - this.pos < 1) {
			throw new EOFException();
		}
		return this.buffer[this.pos++] & 0xFF;
	}
	
	@Override
	public final int readUnsignedShort() throws IOException {

		if (this.count - this.pos < 2) {
			throw new EOFException();
		}
		return ((this.buffer[this.pos++] & 0xff) << 8) + ((this.buffer[this.pos++] & 0xff) << 0);
	}
	
	@Override
	public final String readUTF() throws IOException {

		final int utflen = this.readUnsignedShort();
		final char[] chararr;
		if (utflen <= this.chararr.length) {
			chararr = this.chararr;
		} else {
			chararr = new char[utflen];
		}
		
		int count = 0;
		int chararr_count = 0;
		
		while (count < utflen) {
			if (this.pos >= this.count) {
				throw new IOException("Unexpected end of stream while reading UTF string!");
			}
			final int c = this.buffer[this.pos++] & 0xff;
			if ((c & 0x80) == 0) {
				/* 0xxxxxxx */
				count++;
				chararr[chararr_count++] = (char) c;
				continue;
			}
			if ((c & 0xE0) == 0xC0) {
				/* 110x xxxx 10xx xxxx */
				count += 2;
				if (count > utflen) {
					throw new UTFDataFormatException("malformed input: partial character at end");
				}
				if (this.pos >= this.count) {
					throw new IOException("Unexpected end of stream while reading UTF string!");
				}
				final int char2 = this.buffer[this.pos++] & 0xff;
				if ((char2 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("malformed input around byte " + count);
				}
				chararr[chararr_count++] = (char) ((c & 0x1F) << 6 | char2 & 0x3F);
				continue;
			}
			if ((c & 0xF0) == 0xE0) {
				/* 1110 xxxx 10xx xxxx 10xx xxxx */
				count += 3;
				if (count > utflen) {
					throw new UTFDataFormatException("malformed input: partial character at end");
				}
				if (this.pos >= this.count) {
					throw new IOException("Unexpected end of stream while reading UTF string!");
				}
				final int char2 = this.buffer[this.pos++] & 0xff;
				if (this.pos >= this.count) {
					throw new IOException("Unexpected end of stream while reading UTF string!");
				}
				final int char3 = this.buffer[this.pos++] & 0xff;
				if ((char2 & 0xC0) != 0x80 || (char3 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("malformed input around byte " + (count - 1));
				}
				chararr[chararr_count++] = (char) ((c & 0x0F) << 12 | (char2 & 0x3F) << 6 | (char3 & 0x3F) << 0);
				continue;
			}
			{
				/* 10xx xxxx, 1111 xxxx */
				throw new UTFDataFormatException("malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}
	
	@Override
	public void reset() {

		this.pos = this.mark;
	}
	
	/** Set new data
	 *
	 * @param bytes */
	public void setBytes(final byte[] bytes) {

		this.buffer = bytes;
		this.count = bytes.length;
		this.pos = 0;
		this.mark = 0;
	}
	
	/** Set new data
	 *
	 * @param bytes
	 * @param offset
	 * @param length */
	public void setBytes(final byte[] bytes, final int offset, final int length) {

		this.buffer = bytes;
		this.count = offset + length;
		this.pos = offset;
		this.mark = 0;
	}
	
	@Override
	public long skip(final long count) {

		final int amount = count + this.pos > this.count
			? this.count - this.pos
			: (int) count;
		this.pos += amount;
		return amount;
	}
	
	@Override
	public final int skipBytes(final int n) {

		final int amount = this.count + this.pos > this.count
			? this.count - this.pos
			: (int) this.count;
		this.pos += amount;
		return amount;
	}
	
	/** Equivalent to <code>skip()<code> but takes an int parameter instead of a long, and does not
	 * check whether the count given is larger than the number of remaining bytes.
	 *
	 * @param count
	 * @see #skip(long) */
	public final void skipFast(final int count) {

		this.pos += count;
	}
	
}
