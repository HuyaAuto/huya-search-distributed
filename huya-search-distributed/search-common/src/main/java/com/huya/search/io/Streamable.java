package com.huya.search.io;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.huya.search.util.Util;

/**
 * @author colin.ke keqinwu@163.com
 */
public interface Streamable {

	Map<Class<? extends Streamable>, StreamAbleReader<? extends Streamable>> streamReaders = new ConcurrentHashMap<>();

	class Reader {
		public static StreamAbleReader<? extends Streamable> get(Class<? extends Streamable> clazz) {
			try {
				Class.forName(clazz.getCanonicalName());
			} catch (ClassNotFoundException e) {
				throw Util.newError(e);
			}
			return streamReaders.get(clazz);
		}
	}

	interface StreamAbleReader<S extends Streamable> {

		S read(SearchInputStream inputStream) throws IOException;

	}

	void writeTo(SearchOutputStream out) throws IOException;
}
