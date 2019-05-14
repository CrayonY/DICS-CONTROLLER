package com.ucd.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.UUIDUtils;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesListDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.VO.tdhdsVO.TdhDsVO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesJobVO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.model.TdhTaskParameter;
import com.ucd.server.trapswapApi.ManagerApi.InceptorApi;
import com.ucd.server.trapswapApi.ManagerApi.ServicesApi;
import com.ucd.server.trapswapApi.ManagerApi.UserApi;
import com.ucd.server.trapswapApi.connection.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
    *@ClassName: ServiceDsThread
    *@Description: TODO
    *@Author: gongweimin
    *@CreateDate: 2019/5/5 12:36
    *@Version 1.0
    *@Copyright:  Copyright2018- BJCJ Inc. All rights reserved. 
**/
@Component
public class ServiceDsThread {

    @Value("${basicparameters.transwarp.kafkatime}")
    public String kafkatime;

    @Autowired
    public DaoClient daoClient;

    @Autowired
    public TdhTaskParameterMapper tdhTaskParameterMapper;

    public DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public DateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DateFormat format3=new SimpleDateFormat("yyyy-MM");

    public DateFormat format4=new SimpleDateFormat("yyyyMM");

    private final static Logger logger = LoggerFactory.getLogger(ServiceDsThread.class);

    @Async("transwarpExecutor")
    public void taskSaveDsData(List<TdhServicesJobVO> tdhServicesJobVOList, List<TdhServicesJobDTO> result, String centreDsTableName, String centre, long t1, Date now) {

        logger.info("---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：kafka限制时间："+kafkatime+"h");
        long kafkamsecounds = new Double(Double.valueOf(kafkatime)*3600000).longValue();
        logger.info("---------------------kafka限制时间："+kafkatime+"h|||"+kafkamsecounds+"ms");
        Gson gs = new Gson();
        try {
            int n = 2;//测试
            //return url+"-"+centre+"-"+username+"-"+password;
                    //2.遍历resultjob，根据表名修改对应的job在表中的记录，并且比较resultjob与job的时间差time，如果time大于规定的时间，则将此条记录存入“数据同步”表中
                    //当月的原数据直接存入库中类别为1（snapshot）状态为1（不可见）
                    //当月的数据进行数据拆分（根据时间按照n天一条进行拆分）类别为0（copytable）状态为0（可见）
                    //隔月数据直接存入库中类别为1（snapshot）状态为0（可见）
                    List<TdhDsDTO> tdhDsDTOList = new ArrayList<TdhDsDTO>();
                    List<TdhDsDTO> tdhDsDTOupdateList = new ArrayList<TdhDsDTO>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);
                    int monthNow = calendar.get(Calendar.MONTH) + 1;
                    for (TdhServicesJobDTO tdhServicesJobDTO : result){
                        for (TdhServicesJobVO tdhServicesJobVO : tdhServicesJobVOList){
                             if (tdhServicesJobDTO.getTableName().equals(tdhServicesJobVO.getTableName()) && "running".equals(tdhServicesJobDTO.getStatus()) && "down".equals(tdhServicesJobVO.getStatus())){
                                 long mmsecounds = t1 - tdhServicesJobVO.getHealthtime().getTime();
                                 logger.info(centre + "中心,mmsecounds:"+mmsecounds);
                                 if (mmsecounds > kafkamsecounds){
                                     //需要进行数据同步
                                     //如果now 与 tdhServicesJobVO.getHealthtime()是同一月份则填入一条数据，若是上下两个月，则填入两条数据（snapshot）
                                     calendar.setTime(tdhServicesJobVO.getHealthtime());
                                     int monthVO = calendar.get(Calendar.MONTH) + 1;
                                     if (monthNow == monthVO) {
//                                         logger.info("monthNow == monthVO:"+(monthNow == monthVO));
                                         TdhDsDTO tdhDsDTO = new TdhDsDTO();
                                         tdhDsDTO.setId(KeyUtil.genUniqueKey()+ UUIDUtils.getUUID());
                                         tdhDsDTO.setState(0);
                                         tdhDsDTO.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTO.setTableNameTotal(tdhDsDTO.getTableName()+format4.format(tdhServicesJobVO.getHealthtime()));
                                         tdhDsDTO.setCreattime(now);
                                         tdhDsDTO.setStartupTime(now);
                                         tdhDsDTO.setStartdownTime(tdhServicesJobVO.getHealthtime());
                                         tdhDsDTO.setCentreTableName(centreDsTableName);
                                         tdhDsDTO.setType(0);
                                         tdhDsDTO.setSyncType(2);//total
                                         tdhDsDTO.setCheckStatus(1);//不可见不可操作
                                         tdhDsDTO.setDataMonth(format3.format(tdhServicesJobVO.getHealthtime()));//数据月份
                                         tdhDsDTO.setDataTimes(format2.format(tdhDsDTO.getStartdownTime())+"-"+format2.format(tdhDsDTO.getStartupTime()));
                                         tdhDsDTOList.add(tdhDsDTO);
                                         //根据时间按照n天一条进行拆分
                                         findDates(tdhDsDTOList,tdhDsDTO,tdhServicesJobVO.getHealthtime(),now,n);
                                     }else {
//                                         logger.info("monthNow == monthVO:"+(monthNow == monthVO));
                                         //上个月0s对应的时间
                                         Date startupTime = LastOrFirstSecoundOfMonth(tdhServicesJobVO.getHealthtime(),2);
                                         TdhDsDTO tdhDsDTOtotal1 = new TdhDsDTO();
                                         tdhDsDTOtotal1.setId(KeyUtil.genUniqueKey()+ UUIDUtils.getUUID());
                                         tdhDsDTOtotal1.setState(0);
                                         tdhDsDTOtotal1.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTOtotal1.setTableNameTotal(tdhDsDTOtotal1.getTableName()+format4.format(tdhServicesJobVO.getHealthtime()));
                                         tdhDsDTOtotal1.setCreattime(now);
                                         tdhDsDTOtotal1.setStartupTime(startupTime);
                                         tdhDsDTOtotal1.setStartdownTime(tdhServicesJobVO.getHealthtime());
                                         tdhDsDTOtotal1.setCentreTableName(centreDsTableName);
                                         tdhDsDTOtotal1.setType(0);
                                         tdhDsDTOtotal1.setSyncType(2);//total
                                         tdhDsDTOtotal1.setCheckStatus(1);//可不可见不可操作
                                         tdhDsDTOtotal1.setDataMonth(format3.format(tdhServicesJobVO.getHealthtime()));//数据月份
                                         tdhDsDTOtotal1.setDataTimes(format2.format(tdhDsDTOtotal1.getStartdownTime())+"-"+format2.format(tdhDsDTOtotal1.getStartupTime()));


                                         TdhDsDTO tdhDsDTO1 = new TdhDsDTO();
                                         tdhDsDTO1.setSyncType(1);//snapshot
                                         tdhDsDTO1.setDataMonth(format3.format(tdhServicesJobVO.getHealthtime()));//数据月份
                                         tdhDsDTO1.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTO1.setCentreTableName(centreDsTableName);
                                         ResultVO resultVO = daoClient.getThdDsListData(tdhDsDTO1);
                                         logger.info("resultVO=" + resultVO);
                                         if ("000000".equals(resultVO.getCode())) {
                                             List<TdhDsVO> tdhDsVOS = new ArrayList<TdhDsVO>();
                                             Object object = resultVO.getData();
                                             if (object != null) {
                                              //修改
                                                 String tdhDsVOString = Tools.toJson(object);
                                                 logger.info("tdhDsVOString:" + tdhDsVOString);
                                                 tdhDsVOS = gs.fromJson(tdhDsVOString, new TypeToken<List<TdhDsVO>>() {
                                                 }.getType());
                                                 logger.info("tdhDsVOS:" + tdhDsVOS);
                                                 if (tdhDsVOS == null || tdhDsVOS.size() == 0) {
                                                     //添加
                                                     tdhDsDTO1.setState(0);
                                                     tdhDsDTO1.setCreattime(now);
                                                     tdhDsDTO1.setStartupTime(startupTime);
                                                     tdhDsDTO1.setStartdownTime(tdhServicesJobVO.getHealthtime());
                                                     tdhDsDTO1.setTableNameTotal(tdhDsDTO1.getTableName()+format4.format(tdhServicesJobVO.getHealthtime()));
                                                     tdhDsDTO1.setType(0);
                                                     tdhDsDTO1.setCheckStatus(0);//可见可操作
                                                     tdhDsDTO1.setPid(tdhDsDTOtotal1.getId());
                                                     tdhDsDTO1.setDataTimes(format2.format(tdhDsDTO1.getStartdownTime())+"-"+format2.format(tdhDsDTO1.getStartupTime()));
                                                     tdhDsDTOList.add(tdhDsDTO1);
                                                     tdhDsDTOtotal1.setState(0);
                                                     tdhDsDTOList.add(tdhDsDTOtotal1);
                                                 } else {
                                                     //修改
                                                     TdhDsVO tdhDsVO = tdhDsVOS.get(0);
//                                                     tdhDsDTO1.setState(0);
//                                                     tdhDsDTO1.setCreattime(now);
//                                                     tdhDsDTO1.setStartupTime(startupTime);
//                                                     tdhDsDTO1.setStartdownTime(tdhServicesJobVO.getHealthtime());
//                                                     tdhDsDTO1.setType(0);
//                                                     tdhDsDTO1.setCheckStatus(0);//可见可操作
                                                     tdhDsDTO1.setPid(tdhDsDTOtotal1.getId());
                                                     tdhDsDTO1.setDataTimes(tdhDsVO.getDataTimes()+","+format2.format(tdhServicesJobVO.getHealthtime())+"-"+format2.format(startupTime));
                                                     tdhDsDTO1.setId(tdhDsVO.getId());
                                                     tdhDsDTOupdateList.add(tdhDsDTO1);
                                                     tdhDsDTOtotal1.setState(2);
                                                     tdhDsDTOList.add(tdhDsDTOtotal1);
                                                 }
                                             }else {
                                                 //添加
                                                 tdhDsDTO1.setState(0);
                                                 tdhDsDTO1.setTableNameTotal(tdhDsDTO1.getTableName()+format4.format(tdhServicesJobVO.getHealthtime()));
                                                 tdhDsDTO1.setCreattime(now);
                                                 tdhDsDTO1.setStartupTime(startupTime);
                                                 tdhDsDTO1.setStartdownTime(tdhServicesJobVO.getHealthtime());
                                                 tdhDsDTO1.setType(0);
                                                 tdhDsDTO1.setCheckStatus(0);//可见可操作
                                                 tdhDsDTO1.setPid(tdhDsDTOtotal1.getId());
                                                 tdhDsDTO1.setDataTimes(format2.format(tdhDsDTO1.getStartdownTime())+"-"+format2.format(tdhDsDTO1.getStartupTime()));
                                                 tdhDsDTOList.add(tdhDsDTO1);
                                                 tdhDsDTOtotal1.setState(0);
                                                 tdhDsDTOList.add(tdhDsDTOtotal1);
                                             }
                                         } else {
                                             logger.info( "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                                             throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "中心异常:" + resultVO.getMsg()+resultVO.getData());
                                         }




                                         Date startdownTime = LastOrFirstSecoundOfMonth(tdhServicesJobVO.getHealthtime(),2);//下月0s对应的时间
                                         TdhDsDTO tdhDsDTO2 = new TdhDsDTO();
                                         tdhDsDTO2.setId(KeyUtil.genUniqueKey()+ UUIDUtils.getUUID());
                                         tdhDsDTO2.setState(0);
                                         tdhDsDTO2.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTO2.setTableNameTotal(tdhDsDTO2.getTableName()+format4.format(startdownTime));
                                         tdhDsDTO2.setCreattime(now);
                                         tdhDsDTO2.setStartupTime(now);
                                         tdhDsDTO2.setStartdownTime(startdownTime);
                                         tdhDsDTO2.setCentreTableName(centreDsTableName);
                                         tdhDsDTO2.setType(0);
                                         tdhDsDTO2.setSyncType(2);//total
                                         tdhDsDTO2.setCheckStatus(1);//不可见不可操作
                                         tdhDsDTO2.setDataMonth(format3.format(startdownTime));//数据月份
                                         tdhDsDTO2.setDataTimes(format2.format(tdhDsDTO2.getStartdownTime())+"-"+format2.format(tdhDsDTO2.getStartupTime()));
                                         tdhDsDTOList.add(tdhDsDTO2);
                                         findDates(tdhDsDTOList,tdhDsDTO2,startdownTime,now,n);
                                     }
                                 }
                                 tdhServicesJobDTO.setId(tdhServicesJobVO.getId());
                             }
                        }
                    }
                    if(null == tdhDsDTOList || tdhDsDTOList.size() == 0){
                        logger.info(centre + "中心，没有需要数据同步的数据");
                    }else {
                        //添加“数据同步”数据
                        ResultVO resultVO = new ResultVO();
                        //判断是否是“感知系统”挂导致的mysql数据丢失
//                        TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
//                        tdhTaskParameter.setTaskName("taskServicejob");
//                        List<TdhTaskParameter> tdhTaskParameters = tdhTaskParameterMapper.selectByParameter(tdhTaskParameter);
//                        long taskLastTimems = 0l;
//                        long taskTimems = 0l;
//                        if (tdhTaskParameters.get(0).getTaskLastTime() != null){
//                            taskLastTimems = tdhTaskParameters.get(0).getTaskLastTime().getTime();
//                        }
//                        if (tdhTaskParameters.get(0).getTaskTime() != null){
//                            taskTimems = tdhTaskParameters.get(0).getTaskTime().getTime();
//                        }
//
//                        if (taskTimems - taskLastTimems > kafkamsecounds){//2类数据同步（与“感知系统”挂掉有关）
//                            for (TdhDsDTO tdhDsDTO : tdhDsDTOList){
//                                tdhDsDTO.setType(2);
//                            }
//                            resultVO = daoClient.saveTdhDsData(tdhDsDTOList);
//                        }else {//1类数据同步
//                            resultVO = daoClient.saveTdhDsData(tdhDsDTOList);
//                        }
//                        logger.info(centre + "中心，发送请求");
                        resultVO = daoClient.saveTdhDsData(tdhDsDTOList);
                        if ("000000".equals(resultVO.getCode())) {
                            logger.info(centre + "中心,添加完成"+resultVO.getData().toString());
//                            return;
                        } else {
                            logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                        }
                    }
            if(null == tdhDsDTOupdateList || tdhDsDTOupdateList.size() == 0){
                logger.info(centre + "中心，没有需要修改数据同步的数据");
            }else {
                //修改“数据同步”数据
                ResultVO resultVO = new ResultVO();
                resultVO = daoClient.updateTdhDsInfoByIds(tdhDsDTOupdateList);
                if ("000000".equals(resultVO.getCode())) {
                    logger.info(centre + "中心,添加完成"+resultVO.getData().toString());
//                            return;
                } else {
                    logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                }
            }
        }catch (Exception e){
            logger.info(centre + "中心异常：e="+e.toString());
            return;
        }
    }
/**
 * @author gongweimin
 * @Description        
 * @date 2019/5/5 11:38 
 * @params [date, type]
 * @exception  
 * @return java.util.Date  
 */
    public Date LastOrFirstSecoundOfMonth(Date date,int type) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));//当月最后一天
        String str=format.format(calendar.getTime());
        Date date2 = format.parse(str);//当月最后一天0s
        int dayMis = 1000*60*60*24;
        long curMillisecond = date2.getTime();
        long resultMis = 0;
        if (1 == type){//当月最后一秒59s对应的时间
            resultMis = curMillisecond+(dayMis-1); //当天最后一秒
        }else {//下月0s对应的时间
            resultMis = curMillisecond+dayMis;//下月0s对应的时间
        }
        return new Date(resultMis);
    }

    public  void findDates( List<TdhDsDTO> tdhDsDTOList,TdhDsDTO tdhDsDTO,Date dBegin1, Date dEnd1, int n) throws Exception{
//        logger.info("进入findDates");
        Date dBegin = format.parse(format.format(dBegin1));
        Date dEnd = format.parse(format.format(dEnd1));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd1.after(calBegin.getTime())){
//            logger.info("进入循环");
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            TdhDsDTO tdhDsDTOcopy = new TdhDsDTO();
            tdhDsDTOcopy.setType(0);
            tdhDsDTOcopy.setState(0);
            tdhDsDTOcopy.setTableName(tdhDsDTO.getTableName());
            tdhDsDTOcopy.setCreattime(dEnd1);
            tdhDsDTOcopy.setStartdownTime(calBegin.getTime());
            tdhDsDTOcopy.setCentreTableName(tdhDsDTO.getCentreTableName());
            tdhDsDTOcopy.setSyncType(0);//copytable
            tdhDsDTOcopy.setCheckStatus(0);//可见可操作
            tdhDsDTOcopy.setPid(tdhDsDTO.getId());
            tdhDsDTOcopy.setDataMonth(format3.format(calBegin.getTime()));//数据月份
            tdhDsDTOcopy.setTableNameTotal(tdhDsDTOcopy.getTableName()+format4.format(calBegin.getTime()));
            calBegin.add(Calendar.DAY_OF_MONTH, n);
            tdhDsDTOcopy.setStartupTime(calBegin.getTime());
            tdhDsDTOcopy.setDataTimes(format2.format(tdhDsDTOcopy.getStartdownTime())+"-"+format2.format(tdhDsDTOcopy.getStartupTime()));
            tdhDsDTOList.add(tdhDsDTOcopy);
        }
        tdhDsDTOList.get(tdhDsDTOList.size() - 1).setStartupTime(dEnd1);
        tdhDsDTOList.get(tdhDsDTOList.size() - 1).setDataTimes(format2.format(tdhDsDTOList.get(tdhDsDTOList.size() - 1).getStartdownTime())+"-"+format2.format(tdhDsDTOList.get(tdhDsDTOList.size() - 1).getStartupTime()));
//        logger.info("结束findDates");

    }

    public static void main(String[] args) {
        String a = "insert into station_c2222456sdfg_201812 \n" +
                "SELECT \n" +
                "get_json_object(context_list,'$.Id')||regexp_replace(substr(get_json_object(context_list,'$.Time'),9,15),'(T|\\\\:|\\\\.|\\\\-)',''),\n" +
                "get_json_object(context_list,'$.StringValue'),\n" +
                "get_json_object(context_list,'$.IntValue'),\n" +
                "get_json_object(context_list,'$.DoubleValue'),\n" +
                "case when get_json_object(context_list,'$.BoolValue')='false' THEN 0 when get_json_object(context_list,'$.BoolValue')='true' THEN 1 else null end \n" +
                "from \n" +
                "(select \n" +
                "regexp_replace(regexp_replace(get_json_object(a.stats,'$.PointValues'),'\\,\\\\{\\\"Id\\\"','|\\\\{\\\"Id\\\"'),'(\\\\[|\\\\])','') as context \n" +
                "from station_stream_c a) t lateral view explode(split(t.context,'\\\\|')) context_view as context_list\n" +
                "where get_json_object(context_list,'$.Id') is not null and regexp_replace(substr(get_json_object(context_list,'$.Time'),9,15),'(T|\\\\:|\\\\.|\\\\-)','') is not null and regexp_replace(substr(get_json_object(context_list,'$.Time'),6,2),'(T|\\\\:|\\\\.|\\\\-)','') = substr(201812,5)";
        int index1 = a.indexOf("into ");
        System.out.println("index1:"+index1);//index1:7
        int index2 = a.indexOf("_",a.indexOf("_")+1);
        System.out.println("index2:"+index2);//index2:32
        String b = a.substring(12,index2);
        System.out.println("b:"+b);//b:station_c2222456sdfg
        Date now = new Date();
        long t1 = now.getTime();
        System.out.println("t1:"+t1);//
        System.out.println(a.indexOf("insert"));//0
    }
}
