package ru.myx.io;

/**
 * @author myx
 * 
 *         Just because it's much faster than common java methods like new
 *         String(bytes, charset) etc.
 * 
 */
public class HelperUTF {
	/**
	 * Decodes UTF8 bytes stored in buffer as chars to a character sequence
	 * stored in the same buffer (since UTF8 byte length is equal or longer than
	 * original chars) and returns decoded character count as result.
	 * 
	 * @param buffer
	 * @param length
	 * @return
	 */
	public static final int decodeUTF8(final char[] buffer, final int length) {
		int pos = 0;
		int chararr_count = 0;
		
		while (pos < length) {
			final int c = buffer[pos++] & 0xff;
			if ((c & 0x80) == 0) {
				/* 0xxxxxxx */
				buffer[chararr_count++] = (char) c;
				continue;
			}
			if ((c & 0xE0) == 0xC0) {
				/* 110x xxxx 10xx xxxx */
				if (pos >= length) {
					// IOException
					throw new IllegalArgumentException( "Incomplete UTF data!" );
				}
				final int char2 = buffer[pos++] & 0xff;
				if ((char2 & 0xC0) != 0x80) {
					// UTFDataFormatException
					throw new IllegalArgumentException( "malformed input around byte " + pos );
				}
				buffer[chararr_count++] = (char) ((c & 0x1F) << 6 | char2 & 0x3F);
				continue;
			}
			if ((c & 0xF0) == 0xE0) {
				/* 1110 xxxx 10xx xxxx 10xx xxxx */
				if (pos >= length) {
					// IOException
					throw new IllegalArgumentException( "Incomplete UTF data!" );
				}
				final int char2 = buffer[pos++] & 0xff;
				if (pos >= length) {
					// IOException
					throw new IllegalArgumentException( "Incomplete UTF data!" );
				}
				final int char3 = buffer[pos++] & 0xff;
				if ((char2 & 0xC0) != 0x80 || (char3 & 0xC0) != 0x80) {
					// UTFDataFormatException
					throw new IllegalArgumentException( "malformed input around byte " + (pos - 1) );
				}
				buffer[chararr_count++] = (char) ((c & 0x0F) << 12 | (char2 & 0x3F) << 6 | (char3 & 0x3F) << 0);
				continue;
			}
			{
				/* 10xx xxxx, 1111 xxxx */
				// UTFDataFormatException
				throw new IllegalArgumentException( "malformed input around byte " + pos );
			}
		}
		return chararr_count;
	}
}
