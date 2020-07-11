package com.ucd.server.enums;

import lombok.Getter;

/**
 * 异常枚举类
 * <p>Title: ExceptionEnum</p>
 *
 * @author lx
 * @date
 */
@Getter
public enum SoftwareExceptEnum {
    SUCCESS("000000", "成功"),
    PARAM_ERROR("100001", "传入参数格式错误"),
    GET_SERVICES_STATE_FAILED("300001", "服务状态查询失败"),
    ERROR("500", "内部错误");

    private String code;

    private String message;

    SoftwareExceptEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
