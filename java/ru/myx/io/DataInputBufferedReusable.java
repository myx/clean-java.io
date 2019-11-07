/**
 *
 */
package ru.myx.io;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/** Fast, highly buffered, contains additional control methods. Dedicated for wrapping input
 * streams.
 *
 * @author myx
 *
 *         READ_AHEAD = 4k
 *
 *         BUFFER_MAX = 64k
 *
 *         BUFFER_UTF = 2x16k (1 char array)
 *
 *         TOTAL_BUFFERS = 64k + 2*16k = 96k */
public final class DataInputBufferedReusable extends InputStream implements DataInput {

	private static final InputStream DUMMY_STREAM = new DataInputByteArrayFast(null, 0, 0);

	private final byte[] buffer;

	private final char[] chararr;

	private int count;

	private InputStream in;

	private int pos;

	private int readLimit;

	private boolean readLimitSet;

	private final int readAhead;

	/**
	 *
	 */
	public DataInputBufferedReusable() {

		this.readAhead = 4 * 1024;
		this.buffer = new byte[32 * 1024];
		this.chararr = new char[8 * 1024];
	}
	/** Creates an instance of reusable buffered data input.
	 *
	 * @param readAhead
	 *            1 * 1024 .. 4 * 1024
	 * @param byteBufferCapacity
	 *            16 * 1024 .. 64 * 1024
	 * @param charBufferCapacity
	 *            8 * 1024 .. 16 * 1024 .. 32 * 1024 */
	public DataInputBufferedReusable(final int readAhead, final int byteBufferCapacity, final int charBufferCapacity) {

		this.readAhead = readAhead;
		this.buffer = new byte[byteBufferCapacity];
		this.chararr = new char[charBufferCapacity];
	}

	@Override
	public final int available() throws IOException {

		return this.in.available() + this.count - this.pos;
	}

	@Override
	public final void close() throws IOException {

		this.in.close();
		this.in = null;
	}

	private final void fill() throws IOException {

		this.pos = 0;
		final int read;
		if (this.readLimitSet) {
			read = this.in.read(
					this.buffer,
					0,
					this.readLimit < this.buffer.length
						? this.readLimit
						: this.buffer.length);
			if (read != -1) {
				this.readLimit -= read;
				if (this.readLimit <= 0) {
					this.readLimitSet = false;
				}
			}
		} else {
			read = this.in.read(this.buffer, 0, this.readAhead);
		}
		this.count = read;
	}

	@Override
	public final void mark(final int readlimit) {

		throw new UnsupportedOperationException("unsupported!");
	}

	@Override
	public final boolean markSupported() {

		return false;
	}

	@Override
	public final int read() throws IOException {

		if (this.pos >= this.count) {
			this.fill();
			if (this.pos >= this.count) {
				return -1;
			}
		}
		return this.buffer[this.pos++] & 0xff;
	}

	@Override
	public final int read(final byte b[]) throws IOException {

		return this.read(b, 0, b.length);
	}

	@Override
	public final int read(final byte b[], final int off, final int len) throws IOException {

		if ((off | len | off + len | b.length - (off + len)) < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}

		int n = 0;
		for (;;) {
			final int nread = this.read1(b, off + n, len - n);
			if (nread <= 0) {
				return n == 0
					? nread
					: n;
			}
			n += nread;
			if (n >= len) {
				return n;
			}
			final InputStream input = this.in;
			if (input != null && input.available() <= 0) {
				return n;
			}
		}
	}

	private final int read1(final byte[] b, final int off, final int len) throws IOException {

		int avail = this.count - this.pos;
		if (avail <= 0) {
			if (len >= this.buffer.length) {
				return this.in.read(b, off, len);
			}
			this.fill();
			avail = this.count - this.pos;
			if (avail <= 0) {
				return -1;
			}
		}
		final int amount = avail < len
			? avail
			: len;
		System.arraycopy(this.buffer, this.pos, b, off, amount);
		this.pos += amount;
		return amount;
	}

	@Override
	public final boolean readBoolean() throws IOException {

		if (this.count - this.pos >= 1) {
			return this.buffer[this.pos++] != 0;
		}
		final int ch = this.read();
		if (ch == -1) {
			throw new EOFException();
		}
		return ch != 0;
	}

	@Override
	public final byte readByte() throws IOException {

		if (this.count - this.pos >= 1) {
			return this.buffer[this.pos++];
		}
		final int ch = this.read();
		if (ch == -1) {
			throw new EOFException();
		}
		return (byte) ch;
	}

	@Override
	public final char readChar() throws IOException {

		if (this.count - this.pos >= 2) {
			return (char) (((this.buffer[this.pos++] & 0xff) << 8) + ((this.buffer[this.pos++] & 0xff) << 0));
		}
		final int ch1 = this.read();
		final int ch2 = this.read();
		if (ch2 == -1) {
			throw new EOFException();
		}
		return (char) ((ch1 << 8) + (ch2 << 0));
	}

	@Override
	public final double readDouble() throws IOException {

		return Double.longBitsToDouble(this.readLong());
	}

	@Override
	public final float readFloat() throws IOException {

		return Float.intBitsToFloat(this.readInt());
	}

	@Override
	public final void readFully(final byte b[]) throws IOException {

		final int len = b.length;
		if (this.count - this.pos >= len) {
			System.arraycopy(this.buffer, this.pos, b, 0, len);
			this.pos += len;
			return;
		}
		int n = 0;
		while (n < len) {
			final int count = this.read(b, n, len - n);
			if (count < 0) {
				throw new EOFException();
			}
			n += count;
		}
	}

	@Override
	public final void readFully(final byte b[], final int off, final int len) throws IOException {

		if (len < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (this.count - this.pos >= len) {
			System.arraycopy(this.buffer, this.pos, b, off, len);
			this.pos += len;
			return;
		}
		int n = 0;
		while (n < len) {
			final int count = this.read(b, off + n, len - n);
			if (count < 0) {
				throw new EOFException();
			}
			n += count;
		}
	}

	@Override
	public final int readInt() throws IOException {

		if (this.count - this.pos >= 4) {
			return ((this.buffer[this.pos++] & 0xff) << 24) + ((this.buffer[this.pos++] & 0xff) << 16) + ((this.buffer[this.pos++] & 0xff) << 8)
					+ ((this.buffer[this.pos++] & 0xff) << 0);
		}
		final int ch1 = this.read();
		final int ch2 = this.read();
		final int ch3 = this.read();
		final int ch4 = this.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
	}

	@Override
	@Deprecated
	public final String readLine() {

		throw new UnsupportedOperationException("deprecated");
	}

	@Override
	public final long readLong() throws IOException {

		if (this.count - this.pos >= 8) {
			return ((long) (this.buffer[this.pos++] & 0xff) << 56) + ((long) (this.buffer[this.pos++] & 0xff) << 48) + ((long) (this.buffer[this.pos++] & 0xff) << 40)
					+ ((long) (this.buffer[this.pos++] & 0xff) << 32) + ((long) (this.buffer[this.pos++] & 0xff) << 24) + ((long) (this.buffer[this.pos++] & 0xff) << 16)
					+ ((long) (this.buffer[this.pos++] & 0xff) << 8) + (this.buffer[this.pos++] & 0xff);
		}
		return ((long) this.readUnsignedByte() << 56) + ((long) this.readUnsignedByte() << 48) + ((long) this.readUnsignedByte() << 40) + ((long) this.readUnsignedByte() << 32)
				+ ((long) this.readUnsignedByte() << 24) + (this.readUnsignedByte() << 16) + (this.readUnsignedByte() << 8) + ((this.readUnsignedByte() & 0xff) << 0);
	}

	@Override
	public final short readShort() throws IOException {

		if (this.count - this.pos >= 2) {
			return (short) (((this.buffer[this.pos++] & 0xff) << 8) + ((this.buffer[this.pos++] & 0xff) << 0));
		}
		final int ch1 = this.read();
		final int ch2 = this.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	@Override
	public final int readUnsignedByte() throws IOException {

		if (this.count - this.pos >= 1) {
			return this.buffer[this.pos++] & 0xff;
		}
		final int ch = this.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return ch;
	}

	@Override
	public final int readUnsignedShort() throws IOException {

		if (this.count - this.pos >= 2) {
			return ((this.buffer[this.pos++] & 0xff) << 8) + ((this.buffer[this.pos++] & 0xff) << 0);
		}
		final int ch1 = this.read();
		final int ch2 = this.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (ch1 << 8) + (ch2 << 0);
	}

	@Override
	public final String readUTF() throws IOException {

		final int utflen = this.readUnsignedShort();
		final char[] chararr = utflen <= this.chararr.length
			? this.chararr
			: new char[utflen];

		int count = 0;
		int chararr_count = 0;

		while (count < utflen) {
			if (this.pos >= this.count) {
				this.fill();
				if (this.count < 1) {
					throw new IOException("Unexpected end of stream while reading UTF string!");
				}
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
					this.fill();
					if (this.pos >= this.count) {
						throw new IOException("Unexpected end of stream while reading UTF string!");
					}
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
					this.fill();
					if (this.count < 2) {
						throw new IOException("Unexpected end of stream while reading UTF string!");
					}
				}
				final int char2 = this.buffer[this.pos++] & 0xff;
				if (this.pos >= this.count) {
					this.fill();
					if (this.count < 1) {
						throw new IOException("Unexpected end of stream while reading UTF string!");
					}
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
	public final void reset() {

		throw new UnsupportedOperationException("unsupported!");
	}

	/** Set input bytes
	 *
	 * @param bytes */
	public final void setBytes(final byte[] bytes) {

		this.readLimitSet = false;
		this.pos = 0;
		if (bytes.length <= this.buffer.length) {
			this.in = DataInputBufferedReusable.DUMMY_STREAM;
			this.count = bytes.length;
			System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
		} else {
			this.in = new DataInputByteArrayFast(bytes);
			this.count = 0;
		}
	}

	/** Set input bytes
	 *
	 * @param bytes
	 * @param offset
	 * @param length */
	public final void setBytes(final byte[] bytes, final int offset, final int length) {

		this.readLimitSet = false;
		this.pos = 0;
		if (length <= this.buffer.length) {
			this.in = DataInputBufferedReusable.DUMMY_STREAM;
			this.count = length;
			System.arraycopy(bytes, 0, this.buffer, 0, length);
		} else {
			this.in = new DataInputByteArrayFast(bytes, offset, length);
			this.count = 0;
		}
	}

	/** Set input read limit - to make use of bigger (than default) read aheads.
	 *
	 * @param limit */
	public final void setReadLimit(final int limit) {

		this.readLimitSet = true;
		this.readLimit = limit;
	}

	/** Set input stream
	 *
	 * @param in */
	public final void setStream(final InputStream in) {

		this.readLimitSet = false;
		this.in = in;
		this.pos = 0;
		this.count = 0;
	}

	@Override
	public final long skip(final long n) throws IOException {

		if (n <= 0) {
			return 0;
		}
		final long avail = this.count - this.pos;
		if (avail <= 0) {
			return this.in.skip(n);
		}
		if (avail <= n) {
			this.pos = 0;
			this.count = 0;
			if (avail < n) {
				return avail + this.in.skip(n - avail);
			}
		} else {
			this.pos += n;
		}
		return n;
	}

	@Override
	public final int skipBytes(final int n) throws IOException {

		if (n <= 0) {
			return 0;
		}
		final int avail = this.count - this.pos;
		if (avail <= 0) {
			return (int) this.in.skip(n);
		}
		if (avail <= n) {
			this.pos = 0;
			this.count = 0;
			if (avail < n) {
				return avail + (int) this.in.skip(n - avail);
			}
		} else {
			this.pos += n;
		}
		return n;
	}

}
