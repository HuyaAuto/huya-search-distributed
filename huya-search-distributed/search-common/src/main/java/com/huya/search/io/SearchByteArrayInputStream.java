package com.huya.search.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author colin.ke keqinwu@163.com
 */
public class SearchByteArrayInputStream extends SearchInputStream {

	private final ByteArrayInputStream inputStream;

	public SearchByteArrayInputStream(byte[] bytes) {
		inputStream = new ByteArrayInputStream(bytes);
	}

	@Override
	public byte readByte() throws IOException {
		return (byte) inputStream.read();
	}

	@Override
	public void readBytes(byte[] b, int offset, int len) throws IOException {
		inputStream.read(b, offset, len);
	}

	@Override
	public void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}
}
