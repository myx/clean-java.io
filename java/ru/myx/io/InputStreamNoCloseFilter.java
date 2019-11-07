/**
 * 
 */
package ru.myx.io;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * @author myx
 * 
 */
public final class InputStreamNoCloseFilter extends FilterInputStream {
	/**
	 * @param in
	 */
	public InputStreamNoCloseFilter(final InputStream in) {
		super( in );
	}
	
	@Override
	public void close() {
		// empty
	}
}
