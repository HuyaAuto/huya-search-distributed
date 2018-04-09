package com.huya.search;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class ArdbIllegalStateException extends SearchException {

	public ArdbIllegalStateException() {
		super(null);
	}

	public ArdbIllegalStateException(String msg) {
		super(msg);
	}

	public ArdbIllegalStateException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
