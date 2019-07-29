package com.ucd.server.controller.hardwarecontroller2;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareCpuDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.hardwareservice2.HardWareCPUService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by gwm on 2019/4/1.
 */
@CrossOrigin
@RestController
@RequestMapping("/hardWareCPU2")
public class HardWareCPUController2 {


    @Autowired
    private HardWareCPUService2 hardWareCPUService2;

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(HardwareController2.class);

    /**
     * @author gwm
     * @Description 查看硬件CPU信息
     * @date 2019/3/29 3:03 PM
     * @params [pageView, hardwareCpuDTO]
     * @exception
     * @return com.ucd.common.VO.ResultVO
     */
    @PostMapping(value = "/getHardWareCpu")
    public ResultVO getHardWareCpu(PageView pageView, HardwareCpuDTO hardwareCpuDTO) {

        ResultVO resultVO;
        try {
            if(pageView == null){
                pageView = new PageView();
            }
            pageView = hardWareCPUService2.getHardWareCPU(pageView,hardwareCpuDTO);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),pageView);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }



}
