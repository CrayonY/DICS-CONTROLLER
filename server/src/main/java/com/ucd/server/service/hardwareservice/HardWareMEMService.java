package com.ucd.server.service.hardwareservice;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareMemDTO;

/**
 * Created by crayon on 2019/4/1.
 */
public interface HardWareMEMService {

    /**
     * @return com.ucd.common.utils.pager.PageView
     * @throws
     * @author Crayon
     * @Description 获取硬件的MEM信息
     * @date 2019/4/1 3:07 PM
     * @params [pageView, hardwareMemDTO]
     */
    PageView getHardWareMEM(PageView pageView, HardwareMemDTO hardwareMemDTO) throws Exception;

}
