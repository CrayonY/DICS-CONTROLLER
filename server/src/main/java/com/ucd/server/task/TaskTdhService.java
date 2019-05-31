package com.ucd.server.task;


import com.ucd.client.DaoClient;
import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.model.TdhTaskParameter;
import com.ucd.server.service.impl.ServiceThread;

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

    @Autowired
    public DaoClient daoClient;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static Logger logger = LoggerFactory.getLogger(TaskTdhService.class);
    // 每5秒启动（测试）
    @Scheduled(cron = "3/5 * * * * ?")
    public void timerToNow(){
        System.out.println("now time:" + sdf.format(new Date()));
    }

    //向星环发起请求，获取集群用户信息，并存库
    //@Scheduled(cron = "0-59/28 03-06 18 * * ?")
//    @Scheduled(cron = "0/30 * * * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    public void taskSaveThdUsersListData(){

        Date now = new Date();
        logger.info("taskSaveThdUsersListData()now time:" + sdf.format(now));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("taskState",1);
        map.put("taskName","taskUsersInfo");
        int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
        logger.info("num:" + num);
        if (num == 1) {
            try {
//                now = sdf.parse(sdf.format(now).substring(0,18)+"0");
                logger.info("集群用户信息--成功进入");
                //记录定时任务运行时间
                TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                tdhTaskParameter.setTaskName("taskUsersInfo");
                tdhTaskParameter.setTaskTime(now);
                tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                serviceThread.taskSaveThdUsersListDataThread(serviceinfourla, centrea, usernamea, passworda);
                Thread.sleep(5000);
                map.put("taskState",0);
                tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("集群用户信息--没有进入");
            return;
        }

        return ;
    }

    //向星环发起请求，获取集群服务信息，并存库
    //@Scheduled(cron = "0-59/28 03-06 18 * * ?")
//    @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void taskSaveThdServicesListData(){

        Map<String,Object> map = new HashMap<String,Object>();

        // 初始化数据，进行开门操作
        map.put("taskState",1);
        map.put("taskName","taskServiceInfo");
        int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
        logger.info("num:" + num);

        // 如果返回1，说明开门操作成功，可以进行业务操作
        if (num == 1) {
            try{
                Date now = new Date();
                logger.info("taskSaveThdServicesListData()now time:" + sdf.format(now));
                logger.info("集群服务信息--成功进入");
                // 记录定时任务运行时间
                TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                tdhTaskParameter.setTaskName("taskServiceInfo");
                tdhTaskParameter.setTaskTime(now);
                tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                String nowDate = sdf.format(now).substring(0,18)+"0";
                // 保存A中心数据
                serviceThread.saveThdServicesListDataThread(serviceinfourla, centrea, usernamea, passworda, nowDate);
                // 保存B中心数据
                serviceThread.saveThdServicesListDataThread(serviceinfourlb, centreb, usernameb, passwordb, nowDate);

                // 操作完成进行关门操作
                Thread.sleep(5000);
                map.put("taskState",0);
                tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);

            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            logger.info("集群服务信息--没有进入");
            return;
        }


      /*  Date now = new Date();
        logger.info("taskSaveThdServicesListData()now time:" + sdf.format(now));
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("taskState",1);
            map.put("taskName","taskServiceInfo");
            int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
            logger.info("num:" + num);
            if (num == 1) {
                try {
                logger.info("集群服务信息--成功进入");
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
                logger.info("集群服务信息--没有进入");
                return;
            }
*/
        return ;
    }

    //向星环发起请求，获取running的job信息，并判断是否需要数据同步
//    @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void taskSaveThdServicesJobErrorData(){

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("taskState",1);
        map.put("taskName","taskServicejob");
        int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
        logger.info("num:" + num);
        if (num == 1) {
            try {
                Date now = new Date();
                logger.info("taskSaveThdServicesJobErrorData()now time:" + sdf.format(now));
                now = sdf.parse(sdf.format(now).substring(0,18)+"0");
                logger.info("running的job信息成功进入");
                //记录定时任务运行时间
                TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                tdhTaskParameter.setTaskName("taskServicejob");
                List<TdhTaskParameter> tdhTaskParameters = tdhTaskParameterMapper.selectByParameter(tdhTaskParameter);
                int taskStatus = tdhTaskParameters.get(0).getTaskStatus();
                tdhTaskParameter.setTaskTime(now);
                tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                if (0 == taskStatus){
                    serviceThread.taskSaveThdServicesJobErrorData(joburla, centrea, jobsizea, now);
                    serviceThread.taskSaveThdServicesJobErrorData(joburlb, centreb, jobsizeb, now);
                }
                    Thread.sleep(5000);
                    map.put("taskState", 0);
                    tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("running的job信息没有进入");
            return;
        }

        return ;
    }

    //每个月初将上个月还没有进行完copytable的DS数据（还剩不超过1条数据） 按照表名月份归为1条数据，类别是snapshot
//    @Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 5 0 1 * ?")
    public void taskUpdateThdDsData(){
        Date now = new Date();
        logger.info("taskUpdateThdDsData()now time:" + sdf.format(now));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("taskState",1);
        map.put("taskName","taskDsInfo");
        int num = tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);
        logger.info("num:" + num);
        if (num == 1) {
            try {
                logger.info("修改DS数据成功进入");
                //记录定时任务运行时间
                TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
                tdhTaskParameter.setTaskName("taskDsInfo");
                List<TdhTaskParameter> tdhTaskParameters = tdhTaskParameterMapper.selectByParameter(tdhTaskParameter);
                int taskStatus = tdhTaskParameters.get(0).getTaskStatus();
                tdhTaskParameter.setTaskTime(now);
                tdhTaskParameterMapper.updateTdhServiceTaskTimeByTableName(tdhTaskParameter);
                if (0 == taskStatus){
                    serviceThread.taskUpdateThdDsData(centrea);
                    serviceThread.taskUpdateThdDsData(centreb);
                }
                Thread.sleep(5000);
                map.put("taskState", 0);
                tdhTaskParameterMapper.updateTdhServiceTaskStateMap(map);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("修改DS数据没有进入");
            return;
        }

        return ;
    }

    //自动开门
//    @Scheduled(cron = "5/30 * * * * ?")
    @Scheduled(cron = "5/10 * * * * ?")
    public void taskOpentaskState(){
        Date now = new Date();
        logger.info("taskOpentaskState()now time:" + sdf.format(now));
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
                tables.add("taskUsersInfo");
                //testList();
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



//    public void testList(){
//        logger.info("进入testList");
//        List<String> list = new ArrayList<String>();
//        for(int i = 0;i < 10000000;i++){
//            UUID aa = UUID.randomUUID();
//            list.add(aa.toString());
//        }
//        for (String bb : list){
//            String cc = bb;
//        }
//    }

}
