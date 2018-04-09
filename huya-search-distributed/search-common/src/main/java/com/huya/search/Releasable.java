package com.huya.search;

/**
 * @author colin.ke keqinwu@yy.com
 */
public interface Releasable extends AutoCloseable {

	void close() throws SearchException;

}
