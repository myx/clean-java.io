/**
 * 
 */
package ru.myx.io;

import java.io.OutputStream;

/**
 * @author myx
 * 
 */
public final class OutputStreamCounter extends OutputStream {
	private long	total;
	
	private int		count;
	
	/**
	 * 
	 */
	public OutputStreamCounter() {
		this.total = 0;
		this.count = 0;
	}
	
	private OutputStreamCounter(final long total, final int count) {
		this.total = total;
		this.count = count;
	}
	
	@Override
	protected Object clone() {
		return new OutputStreamCounter( this.total, this.count );
	}
	
	@Override
	public void close() {
		//
	}
	
	/**
	 * Clears counter
	 */
	public void doReset() {
		this.total = this.count = 0;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final OutputStreamCounter other = (OutputStreamCounter) obj;
		if (this.count != other.count) {
			return false;
		}
		if (this.total != other.total) {
			return false;
		}
		return true;
	}
	
	@Override
	public void flush() {
		//
	}
	
	/**
	 * Number of write commands
	 * 
	 * @return
	 */
	public int getCount() {
		return this.count;
	}
	
	/**
	 * Number of bytes written
	 * 
	 * @return
	 */
	public long getTotal() {
		return this.total;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.count;
		result = prime * result + (int) (this.total ^ this.total >>> 32);
		return result;
	}
	
	@Override
	public String toString() {
		return "OutputStreamCounter{total:" + this.total + ",count:" + this.count + "}";
	}
	
	@Override
	public void write(final byte[] b) {
		this.total += b.length;
		this.count++;
	}
	
	@Override
	public void write(final byte[] b, final int off, final int len) {
		this.total += len;
		this.count++;
	}
	
	@Override
	public void write(final int i) {
		this.total++;
		this.count++;
	}
}
