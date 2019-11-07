/**
 * 
 */
package ru.myx.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

import ru.myx.ae3.Engine;

/**
 * Fast, contains additional control methods.
 * 
 * Special method - setPosition() sets write position.
 * 
 * Special method - getPosition() gets current write position.
 * 
 * Special method - getBuffer() returns underlying byte buffer.
 * 
 * Special method - writeFullyFromStream() writes all bytes from given
 * InputStream using own buffers to make this transfer more effective.
 * 
 * @author myx
 * 
 *         BUFFER_CAPACITY = 64k
 * 
 *         BUFFER_UTF = 16k
 * 
 *         TOTAL_BUFFERS = 80k
 */
public final class DataOutputByteArrayReusable extends OutputStream implements DataOutput {
	private static final int	BUFFER_CAPACITY	= Engine.MODE_SIZE
														? 32 * 1024
														: 64 * 1024;
	
	private static final int	BUFFER_UTF		= Engine.MODE_SIZE
														? 4 * 1024
														: 8 * 1024;
	
	private final byte[]		buffer			= new byte[DataOutputByteArrayReusable.BUFFER_CAPACITY];
	
	private final byte[]		bytearr			= new byte[DataOutputByteArrayReusable.BUFFER_UTF];
	
	private int					pos;
	
	/**
	 * Creates an instance of reusable buffered data input.
	 */
	public DataOutputByteArrayReusable() {
		// ignore
	}
	
	@Override
	public final void close() {
		//
	}
	
	@Override
	public void flush() {
		//
	}
	
	/** Flush the internal buffer */
	private void flushBuffer() {
		if (this.pos > 0) {
			throw new ArrayIndexOutOfBoundsException( "Out of buffer!" );
		}
	}
	
	/**
	 * @return underlying byte buffer
	 */
	public byte[] getBuffer() {
		return this.buffer;
	}
	
	/**
	 * @return current write position
	 */
	public int getPosition() {
		return this.pos;
	}
	
	/**
	 * @param position
	 */
	public void setPosition(final int position) {
		this.pos = position;
	}
	
	@Override
	public void write(final byte b[]) {
		final int len = b.length;
		if (len >= DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos) {
			this.flushBuffer();
		}
		System.arraycopy( b, 0, this.buffer, this.pos, len );
		this.pos += len;
	}
	
	@Override
	public void write(final byte b[], final int off, final int len) {
		if (len >= DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos) {
			this.flushBuffer();
		}
		System.arraycopy( b, off, this.buffer, this.pos, len );
		this.pos += len;
	}
	
	/**
	 * Writes the specified byte to this buffered output stream.
	 * 
	 * @param b
	 *            the byte to be written.
	 */
	@Override
	public void write(final int b) {
		if (this.pos >= DataOutputByteArrayReusable.BUFFER_CAPACITY) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = (byte) b;
	}
	
	/**
	 * Writes a <code>boolean</code> to the underlying output stream as a 1-byte
	 * value. The value <code>true</code> is written out as the value
	 * <code>(byte)1</code>; the value <code>false</code> is written out as the
	 * value <code>(byte)0</code>. If no exception is thrown, the counter
	 * <code>written</code> is incremented by <code>1</code>.
	 * 
	 * @param v
	 *            a <code>boolean</code> value to be written.
	 */
	@Override
	public final void writeBoolean(final boolean v) {
		if (this.pos >= DataOutputByteArrayReusable.BUFFER_CAPACITY) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = v
				? (byte) 1
				: (byte) 0;
	}
	
	/**
	 * Writes out a <code>byte</code> to the underlying output stream as a
	 * 1-byte value. If no exception is thrown, the counter <code>written</code>
	 * is incremented by <code>1</code>.
	 * 
	 * @param v
	 *            a <code>byte</code> value to be written.
	 */
	@Override
	public final void writeByte(final int v) {
		if (this.pos >= DataOutputByteArrayReusable.BUFFER_CAPACITY) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = (byte) v;
	}
	
	/**
	 * Writes out the string to the underlying output stream as a sequence of
	 * bytes. Each character in the string is written out, in sequence, by
	 * discarding its high eight bits. If no exception is thrown, the counter
	 * <code>written</code> is incremented by the length of <code>s</code>.
	 * 
	 * @param s
	 *            a string of bytes to be written.
	 */
	@Override
	public final void writeBytes(final String s) {
		final int len = s.length();
		for (int i = 0; i < len; ++i) {
			this.write( (byte) s.charAt( i ) );
		}
	}
	
	/**
	 * Writes a <code>char</code> to the underlying output stream as a 2-byte
	 * value, high byte first. If no exception is thrown, the counter
	 * <code>written</code> is incremented by <code>2</code>.
	 * 
	 * @param v
	 *            a <code>char</code> value to be written.
	 */
	@Override
	public final void writeChar(final int v) {
		if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 2) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = (byte) (v >> 8 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 0 & 0xFF);
	}
	
	/**
	 * Writes a string to the underlying output stream as a sequence of
	 * characters. Each character is written to the data output stream as if by
	 * the <code>writeChar</code> method. If no exception is thrown, the counter
	 * <code>written</code> is incremented by twice the length of <code>s</code>
	 * .
	 * 
	 * @param s
	 *            a <code>String</code> value to be written.
	 * @see java.io.DataOutputStream#writeChar(int)
	 */
	@Override
	public final void writeChars(final String s) {
		final int len = s.length();
		for (int i = 0; i < len; ++i) {
			final int v = s.charAt( i );
			this.write( v >>> 8 & 0xFF );
			this.write( v >>> 0 & 0xFF );
		}
	}
	
	/**
	 * Converts the double argument to a <code>long</code> using the
	 * <code>doubleToLongBits</code> method in class <code>Double</code>, and
	 * then writes that <code>long</code> value to the underlying output stream
	 * as an 8-byte quantity, high byte first. If no exception is thrown, the
	 * counter <code>written</code> is incremented by <code>8</code>.
	 * 
	 * @param v
	 *            a <code>double</code> value to be written.
	 * @see java.lang.Double#doubleToLongBits(double)
	 */
	@Override
	public final void writeDouble(final double v) {
		this.writeLong( Double.doubleToLongBits( v ) );
	}
	
	/**
	 * Converts the float argument to an <code>int</code> using the
	 * <code>floatToIntBits</code> method in class <code>Float</code>, and then
	 * writes that <code>int</code> value to the underlying output stream as a
	 * 4-byte quantity, high byte first. If no exception is thrown, the counter
	 * <code>written</code> is incremented by <code>4</code>.
	 * 
	 * @param v
	 *            a <code>float</code> value to be written.
	 * @see java.lang.Float#floatToIntBits(float)
	 */
	@Override
	public final void writeFloat(final float v) {
		this.writeInt( Float.floatToIntBits( v ) );
	}
	
	/**
	 * Writes all bytes from given InputStream using own buffers to make this
	 * transfer more effective.
	 * 
	 * @param stream
	 * @return amount of bytes transferred
	 * @throws IOException
	 */
	public int writeFullyFromStream(final InputStream stream) throws IOException {
		int written = 0;
		for (;;) {
			if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 1) {
				this.flushBuffer();
			}
			final int read = stream
					.read( this.buffer, this.pos, DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos );
			if (read == -1) {
				break;
			}
			this.pos += read;
			written += read;
		}
		return written;
	}
	
	/**
	 * Writes an <code>int</code> to the underlying output stream as four bytes,
	 * high byte first. If no exception is thrown, the counter
	 * <code>written</code> is incremented by <code>4</code>.
	 * 
	 * @param v
	 *            an <code>int</code> to be written.
	 */
	@Override
	public final void writeInt(final int v) {
		if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 4) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = (byte) (v >> 24 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 16 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 8 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 0 & 0xFF);
	}
	
	/**
	 * Writes a <code>long</code> to the underlying output stream as eight
	 * bytes, high byte first. In no exception is thrown, the counter
	 * <code>written</code> is incremented by <code>8</code>.
	 * 
	 * @param v
	 *            a <code>long</code> to be written.
	 */
	@Override
	public final void writeLong(final long v) {
		if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 8) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = (byte) (v >> 56 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 48 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 40 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 32 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 24 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 16 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 8 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 0 & 0xFF);
	}
	
	/**
	 * Writes a <code>short</code> to the underlying output stream as two bytes,
	 * high byte first. If no exception is thrown, the counter
	 * <code>written</code> is incremented by <code>2</code>.
	 * 
	 * @param v
	 *            a <code>short</code> to be written.
	 */
	@Override
	public final void writeShort(final int v) {
		if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 2) {
			this.flushBuffer();
		}
		this.buffer[this.pos++] = (byte) (v >> 8 & 0xFF);
		this.buffer[this.pos++] = (byte) (v >> 0 & 0xFF);
	}
	
	/**
	 * Writes a string to the underlying output stream using <a
	 * href="DataInput.html#modified-utf-8">modified UTF-8</a> encoding in a
	 * machine-independent manner.
	 * <p>
	 * First, two bytes are written to the output stream as if by the
	 * <code>writeShort</code> method giving the number of bytes to follow. This
	 * value is the number of bytes actually written out, not the length of the
	 * string. Following the length, each character of the string is output, in
	 * sequence, using the modified UTF-8 encoding for the character. If no
	 * exception is thrown, the counter <code>written</code> is incremented by
	 * the total number of bytes written to the output stream. This will be at
	 * least two plus the length of <code>str</code>, and at most two plus
	 * thrice the length of <code>str</code>.
	 * 
	 * @param str
	 *            a string to be written.
	 * @throws UTFDataFormatException
	 */
	@Override
	public final void writeUTF(final String str) throws UTFDataFormatException {
		final byte[] bytearr = this.bytearr;
		int count = 0;
		final int strlen = str.length();
		
		int i = 0;
		for (; i < strlen; ++i) {
			final char c = str.charAt( i );
			if (c >= 0x0001 && c <= 0x007F) {
				if (DataOutputByteArrayReusable.BUFFER_UTF - count < 1) {
					break;
				}
				bytearr[count++] = (byte) c;
				continue;
			}
			if (c > 0x07FF) {
				if (DataOutputByteArrayReusable.BUFFER_UTF - count < 3) {
					break;
				}
				bytearr[count++] = (byte) (0xE0 | c >> 12 & 0x0F);
				bytearr[count++] = (byte) (0x80 | c >> 6 & 0x3F);
				bytearr[count++] = (byte) (0x80 | c >> 0 & 0x3F);
				continue;
			}
			if (DataOutputByteArrayReusable.BUFFER_UTF - count < 2) {
				break;
			}
			bytearr[count++] = (byte) (0xC0 | c >> 6 & 0x1F);
			bytearr[count++] = (byte) (0x80 | c >> 0 & 0x3F);
		}
		
		if (i == strlen) {
			this.writeShort( count );
			this.write( bytearr, 0, count );
		} else {
			final int restart = i;
			int utflen = count;
			/* use charAt instead of copying String to char array */
			for (; i < strlen; ++i) {
				final char c = str.charAt( i );
				if (c >= 0x0001 && c <= 0x007F) {
					++utflen;
				} else //
				if (c > 0x07FF) {
					utflen += 3;
				} else {
					utflen += 2;
				}
			}
			if (utflen > 65535) {
				throw new UTFDataFormatException( "encoded string too long: " + utflen + " bytes" );
			}
			this.writeShort( utflen );
			this.write( bytearr, 0, count );
			for (i = restart; i < strlen; ++i) {
				final char c = str.charAt( i );
				if (c >= 0x0001 && c <= 0x007F) {
					if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 1) {
						this.flushBuffer();
					}
					this.buffer[this.pos++] = (byte) c;
					continue;
				}
				if (c > 0x07FF) {
					if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 3) {
						this.flushBuffer();
					}
					this.buffer[this.pos++] = (byte) (0xE0 | c >> 12 & 0x0F);
					this.buffer[this.pos++] = (byte) (0x80 | c >> 6 & 0x3F);
					this.buffer[this.pos++] = (byte) (0x80 | c >> 0 & 0x3F);
					continue;
				}
				if (DataOutputByteArrayReusable.BUFFER_CAPACITY - this.pos < 2) {
					this.flushBuffer();
				}
				this.buffer[this.pos++] = (byte) (0xC0 | c >> 6 & 0x1F);
				this.buffer[this.pos++] = (byte) (0x80 | c >> 0 & 0x3F);
			}
		}
	}
}
