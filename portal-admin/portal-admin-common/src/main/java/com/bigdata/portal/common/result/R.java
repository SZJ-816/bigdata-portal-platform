package com.bigdata.portal.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    private R() {
    }

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功（无数据） */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /** 成功（带数据） */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /** 成功（带消息和数据） */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    /** 失败（默认消息） */
    public static <T> R<T> fail() {
        return new R<>(500, "操作失败", null);
    }

    /** 失败（自定义消息） */
    public static <T> R<T> fail(String msg) {
        return new R<>(500, msg, null);
    }

    /** 失败（自定义状态码和消息） */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }
}
