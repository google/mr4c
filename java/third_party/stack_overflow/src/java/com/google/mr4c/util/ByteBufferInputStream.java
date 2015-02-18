package com.google.mr4c.util;

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
  * Wraps an InputStream around a ByteBuffer, since the JDK can't be bothered.
  * Copied from: http://stackoverflow.com/questions/4332264/wrapping-a-bytebuffer-with-an-inputstream.
  * The Avro project has an implementation, org.apache.avro.util.ByteBufferInputStream, which has issues, such as throwing EOFException when you reach the end of the buffer
*/
public class ByteBufferInputStream extends InputStream {

	ByteBuffer buf;

	public ByteBufferInputStream(ByteBuffer buf) {
		this.buf = buf;
	}

	public int read() throws IOException {
		if (!buf.hasRemaining()) {
			return -1;
		}
		return buf.get() & 0xFF;
	}

	public int read(byte[] bytes, int off, int len)
			throws IOException {
		if (!buf.hasRemaining()) {
			return -1;
		}

		len = Math.min(len, buf.remaining());
		buf.get(bytes, off, len);
		return len;
	}
}
