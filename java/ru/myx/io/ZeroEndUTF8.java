package ru.myx.io;

/**
 * @author myx
 * 
 */
public final class ZeroEndUTF8 {
	/**
	 * @param source
	 * @param offset
	 * @param limit
	 * @param buffer
	 * @return
	 */
	public static final String decode(final byte[] source, final int offset, final int limit, final char[] buffer) {
		for (int pos = offset, chars = 0;;) {
			final int c = source[pos++] & 0xff;
			if ((c & 0x80) == 0) {
				if (c == 0) {
					return new String( buffer, 0, chars );
				}
				/* 0xxxxxxx */
				buffer[chars++] = (char) c;
				continue;
			}
			if ((c & 0xE0) == 0xC0) {
				/* 110x xxxx 10xx xxxx */
				final int char2 = source[pos++] & 0xff;
				if ((char2 & 0xC0) != 0x80) {
					throw new IllegalArgumentException( "malformed input around byte " + pos );
				}
				buffer[chars++] = (char) ((c & 0x1F) << 6 | char2 & 0x3F);
				continue;
			}
			if ((c & 0xF0) == 0xE0) {
				/* 1110 xxxx 10xx xxxx 10xx xxxx */
				final int char2 = source[pos++] & 0xff;
				final int char3 = source[pos++] & 0xff;
				if ((char2 & 0xC0) != 0x80 || (char3 & 0xC0) != 0x80) {
					throw new IllegalArgumentException( "malformed input around byte " + (pos - 1) );
				}
				buffer[chars++] = (char) ((c & 0x0F) << 12 | (char2 & 0x3F) << 6 | (char3 & 0x3F) << 0);
				continue;
			}
			{
				/* 10xx xxxx, 1111 xxxx */
				throw new IllegalArgumentException( "malformed input around byte " + pos );
			}
		}
	}
	
	/**
	 * @param string
	 * @param offset
	 * @param limit
	 * @param target
	 * @return
	 */
	public static final int encode(final String string, final int offset, final int limit, final byte[] target) {
		final int strlen = string.length();
		int index = offset;
		for (int i = 0; i < strlen; ++i) {
			final char c = string.charAt( i );
			if (c >= 0x0001 && c <= 0x007F) {
				target[index++] = (byte) c;
				continue;
			}
			if (c > 0x07FF) {
				target[index++] = (byte) (0xE0 | c >> 12 & 0x0F);
				target[index++] = (byte) (0x80 | c >> 6 & 0x3F);
				target[index++] = (byte) (0x80 | c >> 0 & 0x3F);
				continue;
			}
			target[index++] = (byte) (0xC0 | c >> 6 & 0x1F);
			target[index++] = (byte) (0x80 | c >> 0 & 0x3F);
		}
		target[index++] = 0;
		return index - offset;
	}
}
