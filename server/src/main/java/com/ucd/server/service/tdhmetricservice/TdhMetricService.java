package com.ucd.server.service.tdhmetricservice;


import com.ucd.daocommon.DTO.tdhmetricDTO.TdhMetricDTO;
import com.ucd.daocommon.VO.tdhmetricVO.MetricInfoVO;

import java.util.List;


public interface TdhMetricService {

    /**
     * @return String
     * @author lx
     * @date
     */
    List<TdhMetricDTO> saveTdhMetric();

    String getClustersMetric(String startTimeStamp, String endTimeStamp, String clusterName);

    String getServicesMetric(String startTimeStamp, String endTimeStamp, String metricName);
}