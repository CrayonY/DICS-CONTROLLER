package com.ucd.server.task;

import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.model.TdhTaskParameter;
import com.ucd.server.service.impl.ServiceThread;
import com.ucd.server.trapswapApi.connection.Connection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Lazy(false)
public class TaskTdhService {

    @Value("${basicparameters.transwarp.serviceinfourla}")
    public String serviceinfourla;
    @Value("${basicparameters.transwarp.joburla}")
    public String joburla;
    @Value("${basicparameters.transwarp.centrea}")
    public String centrea;
    @Value("${basicparameters.transwarp.usernamea}")
    public String usernamea;
    @Value("${basicparameters.transwarp.passworda}")
    public String passworda;
    @Value("${basicparameters.transwarp.serviceinfourlb}")
    public String serviceinfourlb;
    @Value("${basicparameters.transwarp.joburlb}")
    public String joburlb;
    @Value("${basicparameters.transwarp.centreb}")
    public String centreb;
    @Value("${basicparameters.transwarp.usernameb}")
    public String usernameb;
    @Value("${basicparameters.transwarp.passwordb}")
    public String passwordb;
    @Value("${basicparameters.transwarp.jobsizea}")
    public String jobsizea;
    @Value("${basicparameters.transwarp.jobsizeb}")
    public String jobsizeb;

    @Autowired
    public TdhTaskParameterMapper tdhTaskParameterMapper;

    @Autowired
    public ServiceThread serviceThread;

    private final static Logger logger = LoggerFactory.getLogger(TaskTdhService.class);
    // 每5秒启动（测试）
    @Scheduled(cron = "3/5 * * * * ?")
    public void timerToNow(){
        System.out.println("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    //向星环发起请求，获取集群服务信息，并存库
    //@Scheduled(cron = "0-59/28 03-06 18 * * ?")
    //@Scheduled(cron = "0/30 * * * * ?")
    public void taskSaveThdServicesListData(){

        Date now = new Date();
        logger.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("taskState",1);
            map.put("taskName","taskServiceInfo");
            int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
            logger.info("num:" + num);
            if (num == 1) {
                try {
                logger.info("成功进入");
                    //记录定时任务运行时间
                    TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                    tdhTaskParameter.setTaskName("taskServiceInfo");
                    tdhTaskParameter.setTaskTime(now);
                    tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                serviceThread.saveThdServicesListDataThread(serviceinfourla, centrea, usernamea, passworda);
                serviceThread.saveThdServicesListDataThread(serviceinfourlb, centreb, usernameb, passwordb);
                    Thread.sleep(5000);
                    map.put("taskState",0);
                    tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                logger.info("没有进入");
                return;
            }

        return ;
    }

    //向星环发起请求，获取running的job信息，并判断是否需要数据同步
    //@Scheduled(cron = "0/30 * * * * ?")
    public void taskSaveThdServicesJobErrorData(){
        Date now = new Date();
        logger.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("taskState",1);
        map.put("taskName","taskServicejob");
        int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
        logger.info("num:" + num);
        if (num == 1) {
            try {
                logger.info("成功进入");
                //记录定时任务运行时间
                TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                tdhTaskParameter.setTaskName("taskServicejob");
                List<TdhTaskParameter> tdhTaskParameters = tdhTaskParameterMapper.selectByParameter(tdhTaskParameter);
                int taskStatus = tdhTaskParameters.get(0).getTaskStatus();
                tdhTaskParameter.setTaskTime(now);
                tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                if (0 == taskStatus){
                    serviceThread.taskSaveThdServicesJobErrorData(joburla, centrea, jobsizea);
                    serviceThread.taskSaveThdServicesJobErrorData(joburlb, centreb, jobsizeb);
                }
                    Thread.sleep(5000);
                    map.put("taskState", 0);
                    tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("没有进入");
            return;
        }

        return ;
    }

    //自动开门
    //@Scheduled(cron = "5/30 * * * * ?")
    public void taskOpentaskState(){
        Date now = new Date();
        logger.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("taskState",1);
        map.put("taskName","tasktaskDoorkeeper");
        int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
        logger.info("num:" + num);
        if (num == 1) {
            try {
                logger.info("成功进入");
                //记录定时任务运行时间
                TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                tdhTaskParameter.setTaskName("tasktaskDoorkeeper");
                tdhTaskParameter.setTaskTime(now);
                tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                Thread.sleep(2500);
                List tables = new ArrayList();
                tables.add("taskServiceInfo");
                tables.add("taskServicejob");
                tables.add("tasktaskDoorkeeper");
                tdhTaskParameterMapper.updateTdhServiceTaskStateTo0(tables);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("没有进入");
            return;
        }

        return ;
    }



}
