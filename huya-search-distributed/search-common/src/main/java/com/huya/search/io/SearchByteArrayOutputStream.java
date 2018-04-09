package com.huya.search.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author colin.ke keqinwu@163.com
 */
public class SearchByteArrayOutputStream extends SearchOutputStream {

	private final ByteArrayOutputStream outputStream;

	public SearchByteArrayOutputStream() {
		outputStream = new ByteArrayOutputStream();
	}

	@Override
	public void writeByte(byte b) throws IOException {
		outputStream.write(b);
	}

	@Override
	public void writeBytes(byte[] b, int offset, int length) throws IOException {
		outputStream.write(b, offset, length);
	}

	@Override
	public void flush() throws IOException {
		outputStream.flush();
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	@Override
	public void reset() throws IOException {
		outputStream.reset();
	}

	public byte[] toByteArray() {
		return outputStream.toByteArray();
	}
}
