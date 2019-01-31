package com.ucd.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.Tools;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesListDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
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

@Component
public class ServiceDsThread {

    @Value("${basicparameters.transwarp.kafkatime}")
    public String kafkatime;

    @Autowired
    public DaoClient daoClient;

    @Autowired
    public TdhTaskParameterMapper tdhTaskParameterMapper;

    private final static Logger logger = LoggerFactory.getLogger(ServiceDsThread.class);

    @Async("transwarpExecutor")
    public void taskSaveDsData(List<TdhServicesJobVO> tdhServicesJobVOList, List<TdhServicesJobDTO> result, String centreDsTableName, String centre, long t1, Date now) {

        logger.info("---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：kafka限制时间："+kafkatime+"h");
        long kafkamsecounds = new Double(Double.valueOf(kafkatime)*3600000).longValue();
        logger.info("---------------------kafka限制时间："+kafkatime+"h|||"+kafkamsecounds+"ms");
        Gson gs = new Gson();
        try {
            //return url+"-"+centre+"-"+username+"-"+password;
                    //2.遍历resultjob，根据表名修改对应的job在表中的记录，并且比较resultjob与job的时间差time，如果time大于规定的时间，则将此条记录存入“数据同步”表中
                    List<TdhDsDTO> tdhDsDTOList = new ArrayList<TdhDsDTO>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);
                    int monthNow = calendar.get(Calendar.MONTH) + 1;
                    for (TdhServicesJobDTO tdhServicesJobDTO : result){
                        for (TdhServicesJobVO tdhServicesJobVO : tdhServicesJobVOList){
                             if (tdhServicesJobDTO.getTableName().equals(tdhServicesJobVO.getTableName())){
                                 long mmsecounds = t1 - tdhServicesJobVO.getCreattime().getTime();
                                 logger.info(centre + "中心,mmsecounds:"+mmsecounds);
                                 if (mmsecounds > kafkamsecounds){
                                     //需要进行数据同步
                                     //如果now 与 tdhServicesJobVO.getCreattime()是同一月份则填入一条数据，若是上下两个月，则填入两条数据
                                     calendar.setTime(tdhServicesJobVO.getCreattime());
                                     int monthVO = calendar.get(Calendar.MONTH) + 1;
                                     if (monthNow == monthVO) {
                                         TdhDsDTO tdhDsDTO = new TdhDsDTO();
                                         tdhDsDTO.setState(0);
                                         tdhDsDTO.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTO.setCreattime(now);
                                         tdhDsDTO.setStartupTime(now);
                                         tdhDsDTO.setStartdownTime(tdhServicesJobVO.getCreattime());
                                         tdhDsDTO.setCentreTableName(centreDsTableName);
                                         tdhDsDTO.setType(1);
                                         tdhDsDTOList.add(tdhDsDTO);
                                     }else {
                                         Date startupTime = LastOrFirstSecoundOfMonth(tdhServicesJobVO.getCreattime(),1);//当月最后一秒59s对应的时间
                                         TdhDsDTO tdhDsDTO1 = new TdhDsDTO();
                                         tdhDsDTO1.setState(0);
                                         tdhDsDTO1.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTO1.setCreattime(now);
                                         tdhDsDTO1.setStartupTime(startupTime);
                                         tdhDsDTO1.setStartdownTime(tdhServicesJobVO.getCreattime());
                                         tdhDsDTO1.setCentreTableName(centreDsTableName);
                                         tdhDsDTO1.setType(1);
                                         tdhDsDTOList.add(tdhDsDTO1);

                                         Date startdownTime = LastOrFirstSecoundOfMonth(tdhServicesJobVO.getCreattime(),2);//下月0s对应的时间
                                         TdhDsDTO tdhDsDTO2 = new TdhDsDTO();
                                         tdhDsDTO2.setState(0);
                                         tdhDsDTO2.setTableName(tdhServicesJobDTO.getTableName());
                                         tdhDsDTO2.setCreattime(now);
                                         tdhDsDTO2.setStartupTime(now);
                                         tdhDsDTO2.setStartdownTime(startdownTime);
                                         tdhDsDTO2.setCentreTableName(centreDsTableName);
                                         tdhDsDTO2.setType(1);
                                         tdhDsDTOList.add(tdhDsDTO2);
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
                        resultVO = daoClient.saveTdhDsData(tdhDsDTOList);
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

    public Date LastOrFirstSecoundOfMonth(Date date,int type) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));//当月最后一天
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
