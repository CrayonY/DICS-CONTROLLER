package com.ucd.server.service.hardwareservice2;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareNicDTO;

/**
 * Created by crayon on 2019/4/1.
 */
public interface HardWareNICService2 {

    /**
     * @author gwm
     * @Description 获取硬件NIC信息
     * @date 2019/3/29 3:08 PM
     * @params [pageView, hardwareNicDTO]
     * @exception
     * @return com.ucd.common.utils.pager.PageView
     */
    PageView getHardWareNIC(PageView pageView, HardwareNicDTO hardwareNicDTO) throws Exception;
}
