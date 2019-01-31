package com.ucd.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesListDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesJobVO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.service.impl.tdhservicesserviceimpl.TdhServicesServiceImpl;
import com.ucd.server.trapswapApi.ManagerApi.InceptorApi;
import com.ucd.server.trapswapApi.ManagerApi.ServicesApi;
import com.ucd.server.trapswapApi.ManagerApi.UserApi;
import com.ucd.server.trapswapApi.connection.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class ServiceThread {

    @Autowired
    public DaoClient daoClient;

    @Autowired
    public ServiceDsThread serviceDsThread;

    @Autowired
    public TdhTaskParameterMapper tdhTaskParameterMapper;

    private final static Logger logger = LoggerFactory.getLogger(ServiceThread.class);

    @Async("transwarpExecutor")
    public void saveThdServicesListDataThread(String url, String centre, String username, String password) {

        logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
        Connection client = new Connection(url,centre);
        List<TdhServicesInfoDTO> result = new ArrayList<>();
        Date now = new Date();
        Gson gs = new Gson();
        try {
            //return url+"-"+centre+"-"+username+"-"+password;
            if (UserApi.login(client, username, password)) {
                try {
                    String servicesInfo = ServicesApi.getAllServices(client);
                    System.out.println(servicesInfo);
                    result = gs.fromJson(servicesInfo, new TypeToken<List<TdhServicesInfoDTO>>() {
                    }.getType());
                    logger.info(result.toString());
                    UserApi.logout(client);
                    client.close1();
                }catch (Exception e){
                    UserApi.logout(client);
                    client.close1();
                    logger.info(TdhServicesReturnEnum.TDH_CONNECTION_ERROR.getCode()+","+centre + "中心异常：e=" + e);
                    throw new SoftwareException(TdhServicesReturnEnum.TDH_CONNECTION_ERROR.getCode(), centre + "中心异常:" + e.toString());
                }
                logger.info(centre + "中心连接关闭" );
                if (result == null) {
                    logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_NOFOUND);
                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND.getCode(), centre + "中心异常:" + ResultExceptEnum.ERROR_NOFOUND.getMessage());
                }
                for (TdhServicesInfoDTO tdhServicesInfoDTO : result) {
                    tdhServicesInfoDTO.setCentre(centre);//与URL匹配，设置中心
                    String healthChecksId = KeyUtil.genUniqueKey();
                    tdhServicesInfoDTO.setCreattime(now);
                    tdhServicesInfoDTO.setHealthChecksId(healthChecksId);
                    tdhServicesInfoDTO.setTableName("tdha_services_hdfs");
                    logger.info(tdhServicesInfoDTO.toString());
                    List<TdhServicesHealthckDTO> tdhServicesHealthckDTOList = tdhServicesInfoDTO.getHealthChecks();
                    String lastCheck = "";
                    if (null == tdhServicesHealthckDTOList || tdhServicesHealthckDTOList.size() == 0) {
                        logger.info(centre + "中心：tdhServicesHealthckDTOList为空");
                    } else {
                        for (TdhServicesHealthckDTO tdhServicesHealthckDTO : tdhServicesHealthckDTOList) {
                            tdhServicesHealthckDTO.setCreattime(now);
                            tdhServicesHealthckDTO.setHealthChecksId(healthChecksId);
                            logger.info(centre + "中心:"+tdhServicesHealthckDTO.getType()+": time:"+tdhServicesHealthckDTO.getLastCheck());
                            if ("".equals(lastCheck)) {
                                lastCheck = tdhServicesHealthckDTO.getLastCheck();
                            }
                            if("VITAL_SIGN_CHECK".equals(tdhServicesHealthckDTO.getType())){
                                lastCheck = tdhServicesHealthckDTO.getLastCheck();
                            }
                            tdhServicesInfoDTO.setLastCheck(lastCheck);
                            logger.info(tdhServicesHealthckDTO.toString());
                        }
                    }
                    setTableName(tdhServicesInfoDTO);
                    if("SLIPSTREAM".equals(tdhServicesInfoDTO.getType())){
                        //查表“流目前登记的状态”查看流服务的状态state1
                        //判断状态是否是healthy，如果不是，则修改state1的状态为当前状态（若state1的状态与之相同，不修改）；如果是，则判断state1的状态，如果是healthy
                        //则不做操作，如果不是healthy，则修改state1的状态为healthy，且取出当前时间作为stopTime，查表“tdh_services_slipstream”以当前时间为底线，向上
                        //获取第一个状态为healthy的时间作为startTime，如果stopTime-startTime>2.4天（可配置），则将stopTime，startTime并把hbase的对应所有表名（或“ALL”）
                        // 存入到“应该数据同步”的表中，并作状态“未同步”
                    }
                }
                //插入数据库，调用monitor-dao微服务
                TdhServicesListDTO tdhServicesListDTO = new TdhServicesListDTO();
                tdhServicesListDTO.setTdhServicesInfoDTOList(result);
                ResultVO resultVO = daoClient.saveThdServicesListData(tdhServicesListDTO);
                logger.info("resultVO=" + resultVO);
                if ("000000".equals(resultVO.getCode())) {
                    // return resultVO.getData().toString();
                } else {
                    logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                }


            } else {
                logger.info(centre + "中心异常：e=" + TdhServicesReturnEnum.TDH_CODEORPASSWORD_ERROR);
                throw new SoftwareException(TdhServicesReturnEnum.TDH_CODEORPASSWORD_ERROR.getCode(), centre + "中心异常:" + TdhServicesReturnEnum.TDH_CODEORPASSWORD_ERROR.getMessage());
            }
        }catch (Exception e){
            logger.info(centre + "中心异常：e="+e.toString());
            try {
                UserApi.logout(client);
                client.close1();
            }catch (Exception e1){
                logger.info(centre + "中心退出异常：e="+e1.toString());
            }
            return;
        }finally {
            //tdhTaskParameterMapper.updateTdhServiceTaskState(0);
        }
    }

    public void setTableName(TdhServicesInfoDTO tdhServicesInfoDTO){
        String centre = tdhServicesInfoDTO.getCentre();
        String type = tdhServicesInfoDTO.getType();
        if ("A".equals(centre)) {
            if (type == null || "".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_tos");
            } else if ("TOS".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_tos");
            } else if ("LICENSE_SERVICE".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_license");
            } else if ("ZOOKEEPER".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_zookeeper");
            } else if ("KAFKA".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_kafka");
            } else if ("SEARCH".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_search");
            } else if ("MILANO".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_milano");
            } else if ("HDFS".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_hdfs");
            } else if ("YARN".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_yarn");
            } else if ("TXSQL".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_txsql");
            } else if ("INCEPTOR".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_inceptor");
            } else if ("SLIPSTREAM".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_slipstream");
            } else if ("HYPERBASE".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_hyperbase");
            } else if ("PILOT".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_pilot");
            } else if ("TRANSPORTER".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_transporter");
            } else if ("WORKFLOW".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_workflow");
            } else if ("GUARDIAN".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdha_services_guardian");
            }
        }else {
            if (type == null || "".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_tos");
            } else if ("TOS".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_tos");
            } else if ("LICENSE_SERVICE".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_license");
            } else if ("ZOOKEEPER".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_zookeeper");
            } else if ("KAFKA".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_kafka");
            } else if ("SEARCH".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_search");
            } else if ("MILANO".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_milano");
            } else if ("HDFS".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_hdfs");
            } else if ("YARN".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_yarn");
            } else if ("TXSQL".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_txsql");
            } else if ("INCEPTOR".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_inceptor");
            } else if ("SLIPSTREAM".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_slipstream");
            } else if ("HYPERBASE".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_hyperbase");
            } else if ("PILOT".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_pilot");
            } else if ("TRANSPORTER".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_transporter");
            } else if ("WORKFLOW".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_workflow");
            } else if ("GUARDIAN".equals(type)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_guardian");
            }
        }
    }
    @Async("transwarpExecutor")
    public void taskSaveThdServicesJobErrorData(String joburla, String centre, String jobsize) {

        logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
        Connection client = new Connection(joburla,centre);
        List<TdhServicesJobDTO> result = new ArrayList<TdhServicesJobDTO>();
        List<TdhServicesJobVO> tdhServicesJobVOList = new ArrayList<TdhServicesJobVO>();

        Gson gs = new Gson();
        try {
            //return url+"-"+centre+"-"+username+"-"+password;
                try {
                    String jobsInfo = InceptorApi.getjobs(client,0,null,"running",null);
                    System.out.println(jobsInfo);
                    result = gs.fromJson(jobsInfo, new TypeToken<List<TdhServicesJobDTO>>() {
                    }.getType());
//                    logger.info(result.toString());
                    client.close1();
                }catch (Exception e){
                    client.close1();
                    logger.info(TdhServicesReturnEnum.TDH_CONNECTION_ERROR.getCode()+","+centre + "中心异常：e=" + e);
                    throw new SoftwareException(TdhServicesReturnEnum.TDH_CONNECTION_ERROR.getCode(), centre + "中心异常:" + e.toString());
                }
                logger.info(centre + "中心连接关闭" );
                logger.info("result.size():"+result.size());
                if (result.size() == 0) {
                    logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_NOFOUND);
                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND.getCode(), centre + "中心异常:" + ResultExceptEnum.ERROR_NOFOUND.getMessage());
                }
            Date now = new Date();
            long t1 = now.getTime();
            String centreJobTableName = "";
            String centreDsTableName = "";
            if ("B".equals(centre)){
                centreJobTableName = "tdhb_servicesjob_info";
                centreDsTableName = "tdhb_ds_info";
            }else{
                centreJobTableName = "tdha_servicesjob_info";
                centreDsTableName = "tdha_ds_info";
            }
            Iterator<TdhServicesJobDTO> tdhServicesJobIter = result.iterator();
            while(tdhServicesJobIter.hasNext()){
                TdhServicesJobDTO tdhServicesJobDTO = tdhServicesJobIter.next();
                String description = tdhServicesJobDTO.getDescription();
                if (description == null || description.indexOf("insert") != 0){
                    tdhServicesJobIter.remove();
                }else{
                    tdhServicesJobDTO.setCentre(centre);//与URL匹配，设置中心
                    tdhServicesJobDTO.setCentreTableName(centreJobTableName);
                    tdhServicesJobDTO.setCreattime(now);
                    int index2 = description.indexOf("_",description.indexOf("_")+1);
                    String tablename = description.substring(12,index2+1);
                    tdhServicesJobDTO.setTableName(tablename);
                    logger.info(tdhServicesJobDTO.toString());
                }
            }

            //1.查询job表所有job信息，若为空,则判断result的list.size,若size等于规定的数，将所有的result插入数据库，如小于则不做操作。
            TdhServicesJobDTO tdhServicesJobDTO1 = new TdhServicesJobDTO();
            tdhServicesJobDTO1.setCentreTableName(centreJobTableName);
            ResultVO resultVOTdhServicesJobVO = daoClient.getThdServicesjobListData(tdhServicesJobDTO1);
            logger.info("resultVOTdhServicesJobVO=" + resultVOTdhServicesJobVO);
            if("000000".equals(resultVOTdhServicesJobVO.getCode())){
                Object object = resultVOTdhServicesJobVO.getData();
                String tdhServicesJobVOString = Tools.toJson(object);
                logger.info("tdhServicesJobVOString:" + tdhServicesJobVOString);
                tdhServicesJobVOList = gs.fromJson(tdhServicesJobVOString, new TypeToken<List<TdhServicesJobVO>>() {
                }.getType());
                logger.info("tdhServicesJobVOList:" + tdhServicesJobVOList+"  tdhServicesJobVOList.size():"+tdhServicesJobVOList.size());
                if (null == tdhServicesJobVOList || tdhServicesJobVOList.size() == 0) {
                    //job表为空
                    logger.info("job表为空");
                    if(result.size() == Integer.valueOf(jobsize)){
                        ResultVO resultVO = daoClient.saveThdServicesjobListData(result);
                        if ("000000".equals(resultVO.getCode())) {
                            logger.info(centre + "中心,添加完成");
                            return;
                        } else {
                            logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                        }
                    }else{
                        logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + ",result.size()异常:" + result.size());
                        throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:result.size()异常:" + result.size());
                    }
                }else {//job表不为空
                    logger.info("job表不为空");
                    //2.遍历resultjob，根据表名修改对应的job在表中的记录，并且比较resultjob与job的时间差time，如果time大于规定的时间，则将此条记录存入“数据同步”表中
                    serviceDsThread.taskSaveDsData(tdhServicesJobVOList, result, centreDsTableName, centre, t1, now);
                    //修改数据库数据
                    for (TdhServicesJobDTO tdhServicesJobDTO : result) {
                        for (TdhServicesJobVO tdhServicesJobVO : tdhServicesJobVOList) {
                            if (tdhServicesJobDTO.getTableName().equals(tdhServicesJobVO.getTableName())){
                                tdhServicesJobDTO.setId(tdhServicesJobVO.getId());
                            }
                        }
                    }
                    ResultVO resultVO = daoClient.updateThdServicesjobListData(result);
                    if ("000000".equals(resultVO.getCode())) {
                        logger.info(centre + "中心,修改完成:"+resultVO.getData().toString());
                        return;
                    } else {
                        logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                        throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                    }
                }
            }else {
                logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOTdhServicesJobVO.getMsg()+resultVOTdhServicesJobVO.getData());
                throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, centre + "中心异常:" + resultVOTdhServicesJobVO.getMsg()+resultVOTdhServicesJobVO.getData());
            }
        }catch (Exception e){
            logger.info(centre + "中心异常：e="+e.toString());
            try {
                client.close1();
            }catch (Exception e1){
                logger.info(centre + "中心退出异常：e="+e1.toString());
            }
            return;
        }finally {
            //tdhTaskParameterMapper.updateTdhServiceTaskState(0);
        }
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
