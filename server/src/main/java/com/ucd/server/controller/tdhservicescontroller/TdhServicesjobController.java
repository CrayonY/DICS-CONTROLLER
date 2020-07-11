package com.ucd.server.controller.tdhservicescontroller;


import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesJobVO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.tdhservicesservice.TdhServicesjobService;
import com.ucd.server.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/softwarejob")
public class TdhServicesjobController {

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(TdhServicesjobController.class);

    @Autowired
    private TdhServicesjobService tdhServicesjobService;


    @PostMapping(value = "/getThdServicesjobInfo")
    public ResultVO getThdServicesjobInfo(PageView pageView, TdhServicesJobDTO tdhServicesJobDTO) {
        ResultVO resultVO = new ResultVO();
        try {
            if (pageView == null) {
                pageView = new PageView();
            }
            pageView = tdhServicesjobService.getThdServicesjobInfo(pageView, tdhServicesJobDTO);
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


    @PostMapping(value = "/getThdServicesjobListData")
    public ResultVO getThdServicesjobListData(@RequestBody List<TdhServicesJobDTO> tdhServicesJobDTOList) {
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesjobService.getThdServicesjobListData(tdhServicesJobDTOList);
            logger.info("resultVO:" + resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:" + resultVO);
            return resultVO;
        }
    }

    @GetMapping(value = "/test")
    public ResultVO test() {
        ResultVO resultVO = new ResultVO();
        List<TdhServicesJobDTO> tdhServicesJobDTOS = new ArrayList<TdhServicesJobDTO>();
        TdhServicesJobDTO tdhaServicesJobDTO = new TdhServicesJobDTO();
        tdhaServicesJobDTO.setCentreTableName("tdha_servicesjob_info");
        tdhServicesJobDTOS.add(tdhaServicesJobDTO);
        TdhServicesJobDTO tdhbServicesJobDTO = new TdhServicesJobDTO();
        tdhbServicesJobDTO.setCentreTableName("tdhb_servicesjob_info");
        tdhServicesJobDTOS.add(tdhbServicesJobDTO);
        System.out.println(Tools.toJson(tdhServicesJobDTOS));
        try {
            String result = HttpClientUtils.postString("http://10.66.1.192:28070/softwarejob/getThdServicesjobListData", Tools.toJson(tdhServicesJobDTOS), "application/json", null);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(), TdhServicesReturnEnum.SUCCESS.getMessage(), result);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
        }

        return resultVO;
    }

}
