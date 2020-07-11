package com.ucd.server.controller.hardwarecontroller;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareThreadDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.hardwareservice.HardWareThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by crayon on 2019/4/1.
 */
@CrossOrigin
@RestController
@RequestMapping("/hardWareThread")
public class HardWareThreadController {


    @Autowired
    private HardWareThreadService hardWareThreadService;

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(HardWareThreadController.class);

    /**
     * @return com.ucd.common.VO.ResultVO
     * @throws
     * @author Crayon
     * @Description 查看硬件Thread信息
     * @date 2019/3/29 3:03 PM
     * @params [pageView, hardwareThreadDTO]
     */
    @PostMapping(value = "/getHardWareThread")
    public ResultVO getHardWareThread(PageView pageView, HardwareThreadDTO hardwareThreadDTO) {

        ResultVO resultVO;
        try {
            if (pageView == null) {
                pageView = new PageView();
            }
            pageView = hardWareThreadService.getHardWareThread(pageView, hardwareThreadDTO);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(), TdhServicesReturnEnum.SUCCESS.getMessage(), pageView);
            logger.info("resultVO:" + resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:" + resultVO);
            return resultVO;
        }
    }


}
