package com.ucd.server.exception;

import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.exception.BaseException;

public class SoftwareException extends BaseException {
    private Integer code;
    private String message;


    public SoftwareException(String code, String message) {
        super(code, message);
    }

    public SoftwareException(ResultExceptEnum resultEnum) {
        super(resultEnum);
    }

    public SoftwareException(ResultExceptEnum resultEnum, String message) {
        super(resultEnum, message);
    }
}
