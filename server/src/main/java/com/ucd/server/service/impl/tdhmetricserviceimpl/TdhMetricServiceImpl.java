package com.ucd.server.service.impl.tdhmetricserviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ucd.client.DaoClient;

import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.daocommon.DTO.tdhmetricDTO.TdhMetricDTO;
import com.ucd.daocommon.VO.tdhmetricVO.MetricInfoVO;
import com.ucd.server.controller.tdhmetriccontroller.TdhMetricController;
import com.ucd.server.enums.SoftwareExceptEnum;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.tdhmetricservice.RestApiResultService;
import com.ucd.server.service.tdhmetricservice.TdhMetricService;
import com.ucd.server.trapswapApi.ManagerApi.MetricApi;
import com.ucd.server.trapswapApi.ManagerApi.UserApi;
import com.ucd.server.trapswapApi.connection.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TdhMetricServiceImpl implements TdhMetricService {

    private final static Logger logger = LoggerFactory.getLogger(TdhMetricController.class);

    @Autowired
    public RestApiResultService restApiResultService;

    @Autowired
    public DaoClient daoClient;


    @Override
    public List<TdhMetricDTO> saveTdhMetric() {
        List<TdhMetricDTO> result = new ArrayList<>();
        Connection client = new Connection("http://10.28.3.51:8180/api");
        try {
            if (UserApi.login(client, "admin", "admin")) {
                String cpu_used = MetricApi.getClusterMetric(client, 1, "cpu_used", getTimeBeforMills(), getNowTimeMills());
                String disk_ratio = MetricApi.getClusterMetric(client, 1, "disk_ratio", getTimeBeforMills(), getNowTimeMills());
                String mem_used = MetricApi.getClusterMetric(client, 1, "mem_used", getTimeBeforMills(), getNowTimeMills());
                String mem_ratio = MetricApi.getClusterMetric(client, 1, "mem_ratio", getTimeBeforMills(), getNowTimeMills());
                String capacityUsed = MetricApi.getServiceMetric(client, 7, "CapacityUsed", getTimeBeforMills(), getNowTimeMills());
                String capacityRemaining = MetricApi.getServiceMetric(client, 7, "CapacityRemaining", getTimeBeforMills(), getNowTimeMills());
                String dataNodeBytesRead = MetricApi.getServiceMetric(client, 7, "DataNodeBytesRead", getTimeBeforMills(), getNowTimeMills());
                String dataNodeBytesWrite = MetricApi.getServiceMetric(client, 7, "DataNodeBytesWrite", getTimeBeforMills(), getNowTimeMills());
                String hyperBaseNumRegionServers = MetricApi.getServiceMetric(client, 12, "HyperBaseNumRegionServers", getTimeBeforMills(), getNowTimeMills());
                String hyperBaseNumTotalRegion = MetricApi.getServiceMetric(client, 12, "HyperBaseNumTotalRegion", getTimeBeforMills(), getNowTimeMills());
                String hyperBaseReadRequestsRate = MetricApi.getServiceMetric(client, 12, "HyperBaseReadRequestsRate", getTimeBeforMills(), getNowTimeMills());
                String hyperBaseWriteRequestsRate = MetricApi.getServiceMetric(client, 12, "HyperBaseWriteRequestsRate", getTimeBeforMills(), getNowTimeMills());
                String allTopicsBytesInPerSec = MetricApi.getServiceMetric(client, 4, "AllTopicsBytesInPerSec", getTimeBeforMills(), getNowTimeMills());
                String allTopicsBytesOutPerSec = MetricApi.getServiceMetric(client, 4, "AllTopicsBytesOutPerSec", getTimeBeforMills(), getNowTimeMills());
                String allTopicsMessagesInPerSec = MetricApi.getServiceMetric(client, 4, "AllTopicsMessagesInPerSec", getTimeBeforMills(), getNowTimeMills());
                String liveBrokers = MetricApi.getServiceMetric(client, 4, "LiveBrokers", getTimeBeforMills(), getNowTimeMills());
                String deadBrokers = MetricApi.getServiceMetric(client, 4, "DeadBrokers", getTimeBeforMills(), getNowTimeMills());
                System.out.println(cpu_used);
                System.out.println(disk_ratio);
                System.out.println(mem_used);
                System.out.println(mem_ratio);
                System.out.println(capacityUsed);
                System.out.println(capacityRemaining);
                System.out.println(dataNodeBytesRead);
                System.out.println(dataNodeBytesWrite);
                System.out.println(hyperBaseNumRegionServers);
                System.out.println(hyperBaseNumTotalRegion);
                System.out.println(hyperBaseReadRequestsRate);
                System.out.println(hyperBaseWriteRequestsRate);
                System.out.println(allTopicsBytesInPerSec);
                System.out.println(allTopicsBytesOutPerSec);
                System.out.println(allTopicsMessagesInPerSec);
                System.out.println(liveBrokers);
                System.out.println(deadBrokers);
                if (isnotNULL(cpu_used)) {
                    TdhMetricDTO cpuUsedResult = restApiResultService.changeFormatByresult(cpu_used);
                    if (null != cpuUsedResult.getValue()) {
                        result.add(cpuUsedResult);
                    }
                }
                if (isnotNULL(disk_ratio)) {
                    TdhMetricDTO diskRatioResult = restApiResultService.changeFormatByresult(disk_ratio);
                    if (null != diskRatioResult.getValue()) {
                        result.add(diskRatioResult);
                    }
                }
                if (isnotNULL(mem_used.trim())) {
                    TdhMetricDTO memUsedResult = restApiResultService.changeFormatByresult(mem_used);
                    if (null != memUsedResult.getValue()) {
                        result.add(memUsedResult);
                    }
                }
                if (isnotNULL(mem_ratio.trim())) {
                    TdhMetricDTO memRatioResult = restApiResultService.changeFormatByresult(mem_ratio);
                    if (null != memRatioResult.getValue()) {
                        result.add(memRatioResult);
                    }
                }
                if (isnotNULL(capacityUsed.trim())) {
                    TdhMetricDTO capacityUsedResult = restApiResultService.changeFormatByresult(capacityUsed);
                    if (null != capacityUsedResult.getValue()) {
                        result.add(capacityUsedResult);
                    }
                }
                if (isnotNULL(capacityRemaining.trim())) {
                    TdhMetricDTO capacityRemainingResult = restApiResultService.changeFormatByresult(capacityRemaining);
                    if (null != capacityRemainingResult.getValue()) {
                        result.add(capacityRemainingResult);
                    }
                }
                if (isnotNULL(dataNodeBytesRead.trim())) {
                    TdhMetricDTO dataNodeBytesReadResult = restApiResultService.changeFormatByresult(dataNodeBytesRead);
                    if (null != dataNodeBytesReadResult.getValue()) {
                        result.add(dataNodeBytesReadResult);
                    }
                }
                if (isnotNULL(dataNodeBytesWrite.trim())) {
                    TdhMetricDTO dataNodeBytesWriteResult = restApiResultService.changeFormatByresult(dataNodeBytesWrite);
                    if (null != dataNodeBytesWriteResult.getValue()) {
                        result.add(dataNodeBytesWriteResult);
                    }
                }
                if (isnotNULL(hyperBaseNumRegionServers.trim())) {
                    TdhMetricDTO hyperBaseNumRegionServersResult = restApiResultService.changeFormatByresult(hyperBaseNumRegionServers);
                    if (null != hyperBaseNumRegionServersResult.getValue()) {
                        result.add(hyperBaseNumRegionServersResult);
                    }
                }
                if (isnotNULL(hyperBaseNumTotalRegion.trim())) {
                    TdhMetricDTO hyperBaseNumTotalRegionResult = restApiResultService.changeFormatByresult(hyperBaseNumTotalRegion);
                    if (null != hyperBaseNumTotalRegionResult.getValue()) {
                        result.add(hyperBaseNumTotalRegionResult);
                    }
                }
                if (isnotNULL(hyperBaseReadRequestsRate.trim())) {
                    TdhMetricDTO hyperBaseReadRequestsRateResult = restApiResultService.changeFormatByresult(hyperBaseReadRequestsRate);
                    if (null != hyperBaseReadRequestsRateResult.getValue()) {
                        result.add(hyperBaseReadRequestsRateResult);
                    }
                }
                if (isnotNULL(hyperBaseWriteRequestsRate.trim())) {
                    TdhMetricDTO hyperBaseWriteRequestsRateResult = restApiResultService.changeFormatByresult(hyperBaseWriteRequestsRate);
                    if (null != hyperBaseWriteRequestsRateResult.getValue()) {
                        result.add(hyperBaseWriteRequestsRateResult);
                    }
                }
                if (isnotNULL(allTopicsBytesInPerSec.trim())) {
                    TdhMetricDTO allTopicsBytesInPerSecResult = restApiResultService.changeFormatByresult(allTopicsBytesInPerSec);
                    if (null != allTopicsBytesInPerSecResult.getValue()) {
                        result.add(allTopicsBytesInPerSecResult);
                    }
                }
                if (isnotNULL(allTopicsBytesOutPerSec.trim())) {
                    TdhMetricDTO allTopicsBytesOutPerSecResult = restApiResultService.changeFormatByresult(allTopicsBytesOutPerSec);
                    if (null != allTopicsBytesOutPerSecResult.getValue()) {
                        result.add(allTopicsBytesOutPerSecResult);
                    }
                }
                if (isnotNULL(allTopicsMessagesInPerSec.trim())) {
                    TdhMetricDTO allTopicsMessagesInPerSecResult = restApiResultService.changeFormatByresult(allTopicsMessagesInPerSec);
                    if (null != allTopicsMessagesInPerSecResult.getValue()) {
                        result.add(allTopicsMessagesInPerSecResult);
                    }
                }
                if (isnotNULL(liveBrokers.trim())) {
                    TdhMetricDTO liveBrokersResult = restApiResultService.changeFormatByresult(liveBrokers);
                    if (null != liveBrokersResult.getValue()) {
                        result.add(liveBrokersResult);
                    }
                }
                if (isnotNULL(deadBrokers.trim())) {
                    TdhMetricDTO DeadBrokersResult = restApiResultService.changeFormatByresult(deadBrokers);
                    if (null != DeadBrokersResult.getValue()) {
                        result.add(DeadBrokersResult);
                    }
                }
                UserApi.logout(client);
                client.close();
            }
            if (result == null) {
//				logger.error("异常：e=" + ExceptionEnum.ERROR_NOFOUND.getValue());
//				throw new NoFoundExcepiton(ExceptionEnum.ERROR_NOFOUND.getValue());
                logger.error("异常：e=" + ResultExceptEnum.ERROR_NOFOUND);
                throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        //插入数据库，调用monitor-dao微服务
        Date nowtime = new Date();
        for (TdhMetricDTO tdhMetricDTO : result) {
            tdhMetricDTO.setCreattime(nowtime);
        }


        return result;
    }


    @Override
    public String getClustersMetric(String startTimeStamp, String endTimeStamp, String clusterName) {
        String clusterMetric = null;
        Connection client = new Connection("http://10.28.3.51:8180/api");
        try {
            if (UserApi.login(client, "admin", "admin")) {
                clusterMetric = MetricApi.getClusterMetric(client, 1, clusterName, startTimeStamp, endTimeStamp);
                if (isnotNULL(clusterMetric)) {
                    return clusterMetric;
                } else {
                    clusterMetric = TdhServicesReturnEnum.ERROR.getMessage();
                }
            }
            UserApi.logout(client);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            clusterMetric = TdhServicesReturnEnum.ERROR.getMessage();
        }
        return clusterMetric;
    }

    @Override
    public String getServicesMetric(String startTimeStamp, String endTimeStamp, String metricName) {
        String serviceMetric = null;
        Connection client = new Connection("http://10.28.3.51:8180/api");
        try {
            if (UserApi.login(client, "admin", "admin")) {
                serviceMetric = MetricApi.getServiceMetric(client, 1, metricName, startTimeStamp, endTimeStamp);
                if (isnotNULL(serviceMetric)) {
                    return serviceMetric;
                } else {
                    serviceMetric = TdhServicesReturnEnum.ERROR.getMessage();
                }
            }
            UserApi.logout(client);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            serviceMetric = TdhServicesReturnEnum.ERROR.getMessage();
        }
        return serviceMetric;
    }

    public String getNowTimeMills() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String getTimeBeforMills() {
        return String.valueOf(System.currentTimeMillis() - 40000);
    }

    public boolean isnotNULL(String s) {
        if (null == s || ("").equals(s.trim())) {
            return false;
        }
        return true;
    }
}
