package com.ucd.server.enums;

import lombok.Getter;

/**
 * 异常枚举类
 * <p>Title: ExceptionEnum</p>  
 * @author  lx  
 * @date    
 */
@Getter
public enum OperationLogInfoEnum {
    auditThdDsListData("auditThdDsListData","点击按钮，发起数据同步审核"),
    syncThdDsListData("syncThdDsListData","点击按钮，发起数据同步操作"),
    auditTdhDsauditListData("auditTdhDsauditListData","点击按钮，对数据同步请求进行审核");

    private String code;

    private String message;

    OperationLogInfoEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
