package com.ucd.server.service.hardwareservice;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDiskDTO;

/**
 * Created by crayon on 2019/4/1.
 */
public interface HardWareDiskService {

    /**
     * @return com.ucd.common.utils.pager.PageView
     * @throws
     * @author Crayon
     * @Description 获取硬件磁盘信息
     * @date 2019/4/1 3:02 PM
     * @params [pageView, hardwareDiskDTO]
     */
    PageView getHardwareDisk(PageView pageView, HardwareDiskDTO hardwareDiskDTO) throws Exception;

}
