package com.ucd.server.exception;

/**
 * 异常枚举类
 * <p>Title: ExceptionEnum</p>
 *
 * @author lx
 * @date
 */
public enum ExceptionEnum {

    ERROR_NOFOUND("无法找到相应的数据");

    private String value;

    public String getValue() {
        return value;
    }

    ExceptionEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
