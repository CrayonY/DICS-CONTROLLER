package com.ucd.server.service.hardwareservice2;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDiskDTO;

/**
 * Created by crayon on 2019/4/1.
 */
public interface HardWareDiskService2 {

    /**
     * @author gwm
     * @Description 获取硬件磁盘信息
     * @date 2019/4/1 3:02 PM 
     * @params [pageView, hardwareDiskDTO]
     * @exception  
     * @return com.ucd.common.utils.pager.PageView  
     */
    PageView getHardwareDisk(PageView pageView, HardwareDiskDTO hardwareDiskDTO) throws Exception;

}
