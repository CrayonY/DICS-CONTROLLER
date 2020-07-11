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
public enum TdhMetricReturnEnum {

    SUCCESS("000000", "指标查询成功"),
    PARAM_ERROR("100001", "传入参数格式错误"),
    GET_CLUSTERS_METRIC_FAILED("200001", "集群指标查询失败"),
    GET_SERVICES_METRIC_FAILED("200002", "服务指标查询失败"),
    ERROR("500", "内部错误");

    private String code;

    private String message;

    TdhMetricReturnEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
