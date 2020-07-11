package com.ucd.server.controller.hardwarecontroller2;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareMemDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.hardwareservice2.HardWareMEMService2;
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
@RequestMapping("/hardWareMEM2")
public class HardWareMEMController2 {
    @Autowired
    private HardWareMEMService2 hardWareMEMService2;

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(HardWareMEMController2.class);

    /**
     * @return com.ucd.common.VO.ResultVO
     * @throws
     * @author gwm
     * @Description 获取硬件MEM信息
     * @date 2019/4/1 3:41 PM
     * @params [pageView, hardwareMemDTO]
     */
    @PostMapping(value = "/getHardWareMEM")
    public ResultVO getHardWareMEM(PageView pageView, HardwareMemDTO hardwareMemDTO) {
        ResultVO resultVO = null;

        try {
            if (pageView == null) {
                pageView = new PageView();
            }
            // 获取数据
            pageView = hardWareMEMService2.getHardWareMEM(pageView, hardwareMemDTO);
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
