package com.ucd.server.service.operationloginfoservice;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.operationLogInfoDTO.OperationLogInfoDTO;

public interface OperationLogInfoService {
    public String saveOperationLogInfo(OperationLogInfoDTO operationLogInfoDTO) throws Exception;

    PageView getOperationLogInfo(PageView pageView, OperationLogInfoDTO operationLogInfoDTO) throws Exception;
}
