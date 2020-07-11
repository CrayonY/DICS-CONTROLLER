package com.ucd.server.controller.tdhservicescontroller;


import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultEnum;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;

import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.tdhservicesservice.TdhServicesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/software")
public class TdhServicesController {

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(TdhServicesController.class);

    @Autowired
    private TdhServicesService tdhServicesService;

//    @GetMapping(value = "/saveThdServices")
//    public ResultVO saveThdServicesData(HttpServletResponse httpServletResponse) {
//        logger.info("进入controller啦——————————————");
//        List<TdhServicesInfoDTO> result = null;
//        ResultVO resultVO = new ResultVO();
//        try {
//            result = tdhServicesService.saveThdServicesData();
//            resultVO = ResultVOUtil.setResult(ResultEnum.RESULT_SUCCESS.getCode(), ResultEnum.RESULT_SUCCESS.getMessage(),result);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultVO = ResultVOUtil.error(e);
//        }
//
//        return resultVO;
//    }

    //测试使用
    @GetMapping(value = "/saveThdServicesList")
    public ResultVO saveThdServicesListData(HttpServletResponse httpServletResponse) {
        logger.info("进入controller啦——————————————");
        ResultVO resultVO = new ResultVO();
        try {
            String result = tdhServicesService.saveThdServicesListData();
            resultVO = ResultVOUtil.setResult(ResultEnum.RESULT_SUCCESS.getCode(), ResultEnum.RESULT_SUCCESS.getMessage(), result);

        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
        }

        return resultVO;
    }

    @PostMapping(value = "/getThdServicesInfo")
    public ResultVO getThdServicesInfo(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) {
        ResultVO resultVO = new ResultVO();
        try {
            if (pageView == null) {
                pageView = new PageView();
            }
            pageView = tdhServicesService.getThdServicesInfo(pageView, tdhServicesInfoDTO);
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

    //点亮“数据同步”按钮（可用状态）
    @GetMapping(value = "/saveThdServicesLocalDao")
    public ResultVO updateDataSynchronizationState(HttpServletResponse httpServletResponse) {
        logger.info("进入controller啦——————————————");
        ResultVO resultVO = new ResultVO();
        String centre = "A";
        try {
            String result = tdhServicesService.updateDataSynchronizationState(centre);
            resultVO = ResultVOUtil.setResult(ResultEnum.RESULT_SUCCESS.getCode(), ResultEnum.RESULT_SUCCESS.getMessage(), result);

        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
        }

        return resultVO;
    }


    /**
     * @return com.ucd.common.utils.pager.PageView
     * @throws
     * @author Crayon
     * @Description 查看所有实时表数据
     * @date 2019/4/16 3:11 PM
     * @params [pageView, tdhServicesInfoDTO]
     */
    @PostMapping(value = "/getThdServicesListNow")
    public ResultVO getThdServicesListNow(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) {
        logger.info("tdhServicesInfoDTO:" + tdhServicesInfoDTO);
        ResultVO resultVO;
        try {
            if (pageView == null) {
                pageView = new PageView();
            }
            pageView = tdhServicesService.getThdServicesListNow(pageView, tdhServicesInfoDTO);
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


    /**
     * @return com.ucd.common.utils.pager.PageView
     * @throws
     * @author Crayon
     * @Description 服务健康状态监测
     * @date 2019/4/19 10:19 AM
     * @params [pageView, second, startTime, endTime]
     */
    @PostMapping(value = "/getTdhHealthStatus")
    public ResultVO getTdhHealthStatus(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) {
        ResultVO resultVO = new ResultVO();
        try {
            if (pageView == null) {
                pageView = new PageView();
            }
            pageView = tdhServicesService.getTdhHealthStatus(pageView, tdhServicesInfoDTO);
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

    //    @GetMapping(value = "/saveThdServicesLocalDao")
//    public ResultVO saveThdServicesLocalDao(HttpServletResponse httpServletResponse) {
//        logger.info("进入controller啦——————————————");
//        List<com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO> result = null;
//        ResultVO resultVO = new ResultVO();
//        try {
//            result = tdhServicesService.saveThdServicesLocalDao();
//            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultVO = ResultVOUtil.error(e);
//        }
//
//        return resultVO;
//    }


}
