package com.huya.search;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class SearchException extends RuntimeException {

	/**
	 * Construct a <code>ElasticsearchException</code> with the specified detail message.
	 *
	 * @param msg the detail message
	 */
	public SearchException(String msg) {
		super(msg);
	}

	/**
	 * Construct a <code>ElasticsearchException</code> with the specified detail message
	 * and nested exception.
	 *
	 * @param msg   the detail message
	 * @param cause the nested exception
	 */
	public SearchException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
