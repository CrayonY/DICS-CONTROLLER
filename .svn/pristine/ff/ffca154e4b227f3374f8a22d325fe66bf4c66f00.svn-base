package com.ucd.server.enums;

import lombok.Getter;

/**
 * 异常枚举类
 * <p>Title: ExceptionEnum</p>  
 * @author  lx  
 * @date    
 */
@Getter
public enum TdhServicesReturnEnum {

    SUCCESS("000000","服务成功"),
    PARAM_ERROR("100001","传入参数格式错误"),
    TDH_CODEORPASSWORD_ERROR("200001","星环登陆用户或密码错误"),
    GET_SERVICES_STATE_FAILED("300001","服务状态查询失败"),
    TDH_CONNECTION_ERROR("400001","星环异常响应"),
    ERROR("500","内部错误");

    private String code;

    private String message;

    TdhServicesReturnEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
