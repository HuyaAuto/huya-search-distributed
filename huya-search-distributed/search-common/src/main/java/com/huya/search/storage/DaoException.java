package com.huya.search.storage;

public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 3053089041278490842L;

    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String msg, Exception ex) {
        super(msg, ex);
    }
}
