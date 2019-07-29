package com.ucd.server.service.hardwareservice2;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareThreadDTO;

/**
 * Created by crayon on 2019/4/1.
 */
public interface HardWareThreadService2 {

    /**
     * @author gwm
     * @Description 获取硬件Thread信息
     * @date 2019/3/29 3:08 PM
     * @params [pageView, hardwareThreadDTO]
     * @exception
     * @return com.ucd.common.utils.pager.PageView
     */
    PageView getHardWareThread(PageView pageView, HardwareThreadDTO hardwareThreadDTO) throws Exception;
}
