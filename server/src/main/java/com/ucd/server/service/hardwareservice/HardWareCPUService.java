package com.ucd.server.service.hardwareservice;

import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareCpuDTO;

/**
 * Created by crayon on 2019/4/1.
 */
public interface HardWareCPUService {

    /**
     * @return com.ucd.common.utils.pager.PageView
     * @throws
     * @author Crayon
     * @Description 获取硬件CPU信息
     * @date 2019/3/29 3:08 PM
     * @params [pageView, hardwareCpuDTO]
     */
    PageView getHardWareCPU(PageView pageView, HardwareCpuDTO hardwareCpuDTO) throws Exception;
}
