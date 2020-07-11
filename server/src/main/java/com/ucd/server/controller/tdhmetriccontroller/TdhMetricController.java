package com.ucd.server.controller.tdhmetriccontroller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;


import com.ucd.common.VO.ResultVO;
import com.ucd.common.exception.BaseException;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.daocommon.DTO.tdhmetricDTO.TdhMetricDTO;
import com.ucd.daocommon.VO.tdhmetricVO.MetricInfoVO;
import com.ucd.server.enums.TdhMetricReturnEnum;
import com.ucd.server.service.tdhmetricservice.TdhMetricService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/tdhMetric")
public class TdhMetricController {

    @Autowired
    private TdhMetricService tdhMetricService;

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(TdhMetricController.class);

    /**
     * @param httpServletResponse
     * @return String
     * @throws Exception
     * @author lx
     * @date
     */
    @GetMapping(value = "/saveTdhMetric")
    public String saveTdhMetric(HttpServletResponse httpServletResponse) throws Exception {
        logger.info("进入controller啦——————————————");
        List<TdhMetricDTO> result = tdhMetricService.saveTdhMetric();
        if (result == null) {
            return "数据插入异常";

        }
        return "数据已成功插入！";
    }

    @GetMapping(value = "/getMetricAllInfo")
    public ResultVO getMetricAllInfo() {
        return null;
    }

    @GetMapping(value = "/getClustersMetric")
    public ResultVO getClustersMetric(@RequestParam(value = "startTimeStamp", required = true) String startTimeStamp,
                                      @RequestParam(value = "endTimeStamp", required = true) String endTimeStamp,
                                      @RequestParam(value = "clusterName", required = true) String clusterName) throws Exception {
        String clusterInfoVOList = tdhMetricService.getClustersMetric(startTimeStamp, endTimeStamp, clusterName);
        if (clusterInfoVOList == null) {
            ResultVOUtil.setResult(TdhMetricReturnEnum.PARAM_ERROR.getCode(), TdhMetricReturnEnum.PARAM_ERROR.getMessage(), null);
        }

        return ResultVOUtil.setResult(TdhMetricReturnEnum.SUCCESS.getCode(), TdhMetricReturnEnum.SUCCESS.getMessage(), clusterInfoVOList);
    }

    @GetMapping(value = "/getServicesMetric")
    public ResultVO getServicesMetric(@RequestParam(value = "startTimeStamp", required = true) String startTimeStamp,
                                      @RequestParam(value = "endTimeStamp", required = true) String endTimeStamp,
                                      @RequestParam(value = "metricName", required = true) String metricName) throws Exception {
        String metricInfoVOList = tdhMetricService.getServicesMetric(startTimeStamp, endTimeStamp, metricName);
        if (metricInfoVOList == null) {
            ResultVOUtil.setResult(TdhMetricReturnEnum.PARAM_ERROR.getCode(), TdhMetricReturnEnum.PARAM_ERROR.getMessage(), null);
        }

        return ResultVOUtil.setResult(TdhMetricReturnEnum.SUCCESS.getCode(), TdhMetricReturnEnum.SUCCESS.getMessage(), metricInfoVOList);
    }
}
