package com.bigdata.portal.common.exception;

import com.bigdata.portal.common.result.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 处理自定义业务异常 */
    @ExceptionHandler(CustomException.class)
    public R<Void> handleCustomException(CustomException e) {
        log.warn("业务异常：code={}, msg={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /** 处理参数校验异常 */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常：{}", e.getMessage());
        return R.fail(400, e.getMessage());
    }

    /** 处理其他运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return R.fail("系统内部错误，请联系管理员");
    }

    /** 处理所有未捕获异常 */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return R.fail("系统内部错误，请联系管理员");
    }
}
