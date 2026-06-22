package com.bigdata.portal.common.exception;

import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误状态码 */
    private final int code;

    public CustomException(String message) {
        super(message);
        this.code = 500;
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
