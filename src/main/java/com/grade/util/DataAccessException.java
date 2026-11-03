package com.grade.util;

/**
 * 数据访问异常，把受检的 SQLException 包装成运行时异常，让上层代码更清爽。
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
