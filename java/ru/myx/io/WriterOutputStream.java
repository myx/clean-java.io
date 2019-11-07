package ru.myx.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author myx
 * 
 */
public class WriterOutputStream extends OutputStream {
	
	private final Writer			writer;
	
	private final CharsetDecoder	decoder;
	
	private final CharBuffer		charBuffer;
	
	private final ByteBuffer		byteBuffer;
	
	/**
	 * @param writer
	 * @param charset
	 */
	public WriterOutputStream(final Writer writer, final Charset charset) {
		this.writer = writer;
		this.decoder = charset.newDecoder();
		this.charBuffer = CharBuffer.allocate( 256 );
		this.byteBuffer = ByteBuffer.allocate( 256 );
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
		super.close();
	}
	
	@Override
	public void write(final int b) throws IOException {
		System.out.println( ">>>>>> writeByte: " + this.byteBuffer.remaining() );
		this.byteBuffer.put( (byte) b );
		this.byteBuffer.flip();
		System.out.println( ">>>>>> writeByte result: " + this.byteBuffer.remaining() );
		
		this.charBuffer.clear();
		this.decoder.decode( this.byteBuffer, this.charBuffer, false );
		this.byteBuffer.compact();
		this.charBuffer.flip();
		while (this.charBuffer.hasRemaining()) {
			this.writer.write( this.charBuffer.get() );
		}
	}
	
}
