package com.huya.search.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class Streams {

	public static final int BUFFER_SIZE = 1024 * 8;

	public static String copyToString(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}

	/**
	 * Copy the contents of the given Reader to the given Writer.
	 * Closes both when done.
	 *
	 * @param in  the Reader to copy from
	 * @param out the Writer to copy to
	 * @return the number of characters copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(Reader in, Writer out) throws IOException {
		try {
			int byteCount = 0;
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				// do nothing
			}
			try {
				out.close();
			} catch (IOException ex) {
				// do nothing
			}
		}
	}
}
