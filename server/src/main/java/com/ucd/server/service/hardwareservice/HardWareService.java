package com.ucd.server.service.hardwareservice;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDTO;

public interface HardWareService {
    public String saveHardWareInfo(HardwareDTO hardwareDTO) throws Exception;

    PageView gethardwareInfo(PageView pageView, HardwareDTO hardwareDTO) throws Exception;
}
