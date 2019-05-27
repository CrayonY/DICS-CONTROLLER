package com.ucd.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.StringTool;
import com.ucd.common.utils.Tools;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesListDTO;
import com.ucd.daocommon.DTO.userDTO.UserDTO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesAVO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesJobVO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.mapper.TdhTaskParameterMapper;
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
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceThread {


    @Value("${basicparameters.transwarp.namenuma}")
    public String nameNumA;

    @Value("${basicparameters.transwarp.namenumb}")
    public String nameNumB;

    @Value("${basicparameters.transwarp.service-runtime}")
    public String serviceRuntime;

    @Value("${basicparameters.transwarp.centrea}")
    public String centrea;

    @Value("${basicparameters.transwarp.centreb}")
    public String centreb;

    @Value("${basicparameters.transwarp.service-name-a}")
    public String serviceNameA;

    @Value("${basicparameters.transwarp.service-name-b}")
    public String serviceNameB;

    private final Integer NUM = 180;

    /** 状态：健康 */
    private final String HEALTHY = "HEALTHY";

    /** 状态：未知 */
    private final String UNKNOW = "UNKNOW";


    private final String TDHA_SERVICES_INFO_NOW = "tdha_services_info_now";


    private final String TDHB_SERVICES_INFO_NOW = "tdhb_services_info_now";


    @Autowired
    public DaoClient daoClient;

    @Autowired
    public ServiceDsThread serviceDsThread;

    @Autowired
    public TdhTaskParameterMapper tdhTaskParameterMapper;


    private final static Logger logger = LoggerFactory.getLogger(ServiceThread.class);

    /**
     * @author Crayon
     * @Description
     * @date 2019/4/12 4:57 PM
     * @params [url, centre, username, password]
     * @exception
     * @return void
     */
    @Async("transwarpExecutor")
    public void saveThdServicesListDataThread(String url, String centre, String username, String password) {

        logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
        Connection client = new Connection(url,centre);
        List<TdhServicesInfoDTO> result = new ArrayList<>();
        Date now = new Date();
        Gson gs = new Gson();

        // 当前时间转字符串
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowDate = ldt.format(dateTimeFormatter);
        try{

            if(UserApi.login(client, username, password)){
                try{
                    String servicesInfo = ServicesApi.getAllServices(client);
//                    logger.info(servicesInfo.toString());

                    // 格式化 第三方返回结果
                    result = gs.fromJson(servicesInfo, new TypeToken<List<TdhServicesInfoDTO>>() {
                    }.getType());
//                    logger.info(result.toString());
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
                logger.info(centre + "中心:result.size"+ result.size());
                ResultVO resultVO = null;
                // 1.获取查询实时数据结果
                TdhServicesAVO thdServicesInfoNow = daoClient.getThdServicesInfoNow(centre);
                // 当前秒数
                String healthChecksIdNum = (String) StringTool.parsentObjectNull(thdServicesInfoNow.getHealthChecksId());
                // 插入数据库，调用monitor-dao微服务，进行相应逻辑判断
                TdhServicesListDTO tdhServicesListDTO = new TdhServicesListDTO();

                // 补全未知数据，调用monitor-dao微服务，进行相应逻辑判断
                TdhServicesListDTO tdhServicesUnknowNameListDTO = new TdhServicesListDTO();

                // 筛选接口返回的服务类型
//                List<String> typeList = result.parallelStream().map(TdhServicesInfoDTO::getType).collect(Collectors.toList());
                List<String> nameList = result.parallelStream().map(TdhServicesInfoDTO::getName).collect(Collectors.toList());

                /** 如果结果为空,证明第一次执行定时任务，直接插入第一条实时数据 */
                if(ObjectUtils.isEmpty(healthChecksIdNum)){
                    // 保存第一条实时服务数据
                    tdhServicesListDTO.setTdhServicesInfoDTOList(result);
                    result = this.healthChecksIdNumIsEmpty(result,tdhServicesListDTO,centre, now,nowDate,thdServicesInfoNow,healthChecksIdNum,tdhServicesUnknowNameListDTO,resultVO);

                    // 筛选result中不健康的数据
                    result = result.stream().filter(a -> !a.getHealth().equals(HEALTHY)).collect(Collectors.toList());
                }

                /** 如果服务实时表已有数据或者初始化数据成功后，执行以下程序 */
                List<String> nameAorBList = new ArrayList<>();
                // 查看所有服务类型
                if (centre.equals(centrea)){
                    nameAorBList = StringTool.stringToStrList(serviceNameA,",");
                }
                if (centre.equals(centreb)){
                    nameAorBList = StringTool.stringToStrList(serviceNameB,",");
                }
                // 筛选所有未知状态的服务
                ArrayList<String> unknowNameList = new ArrayList<>();
                List<String> finalNameAorBList = nameAorBList;
                logger.info("tttttttttttttttttttttttt"+nameList);
                finalNameAorBList.forEach(allName -> {
                    if(nameList.parallelStream().noneMatch(unknowName -> unknowName.equals(allName))){
                        // 简化版 if(typeList.parallelStream().noneMatch(allType::equals)){
                        unknowNameList.add(allName);
                    }
                });
                logger.info("11111111111"+unknowNameList.toString());
                // 简化版写法
                List<String> unknowNameList1 = finalNameAorBList.parallelStream().filter(allName ->
                        nameList.parallelStream().noneMatch(allName::equals)).collect(Collectors.toList());

                /** 如果结果不为空，更新实时数据,并补全缺少未知状态数据  */
                if(!ObjectUtils.isEmpty(healthChecksIdNum)){
                    result = this.healthChecksIdNumNotIsEmpty(result,tdhServicesListDTO,centre, now,nowDate,thdServicesInfoNow,healthChecksIdNum,unknowNameList,tdhServicesUnknowNameListDTO,resultVO);

                    /** 判断如果不为180s时，只存储不健康数据 */
                    if(Integer.parseInt(thdServicesInfoNow.getHealthChecksId()) != NUM){
                        // 筛选result中不健康的数据
                        result = result.stream().filter(a -> !a.getHealth().equals(HEALTHY)).collect(Collectors.toList());
                    }
                }
                logger.info("333333333333333333333"+result.size());
                if(result != null && result.size()>0){
                    // 数据初始化
                    result.forEach(tdhServicesInfoDTO -> {
                        logger.info("2222222222222222222222222222"+tdhServicesInfoDTO.getName());
                        // 与URL匹配，设置中心，初始化数据
                        tdhServicesInfoDTO.setCentre(centre);
                        String healthChecksId = KeyUtil.genUniqueKey();
                        tdhServicesInfoDTO.setCreattime(now);
                        tdhServicesInfoDTO.setTaskTime(nowDate);
                        tdhServicesInfoDTO.setHealthChecksId(healthChecksId);
                        if ("A".equals(centre)) {
                            tdhServicesInfoDTO.setTableName("tdha_services_TOS");
                        }else {
                            tdhServicesInfoDTO.setTableName("tdhb_services_TOS");
                        }
//                        logger.info(tdhServicesInfoDTO.toString());

                        // 获取第三方json串中：healthChecks数据
                        List<TdhServicesHealthckDTO> tdhServicesHealthckDTOList = tdhServicesInfoDTO.getHealthChecks();
                        // 查询时间
                        String lastCheck = "";
                        if (null == tdhServicesHealthckDTOList || tdhServicesHealthckDTOList.size() == 0) {
                            logger.info(centre + "中心：tdhServicesHealthckDTOList为空");
                        }else {
                            // 获取healthChecks中所需信息，并初始化信息
                            for (TdhServicesHealthckDTO tdhServicesHealthckDTO : tdhServicesHealthckDTOList) {
                                tdhServicesHealthckDTO.setCreattime(now);
                                tdhServicesInfoDTO.setTaskTime(nowDate);
                                tdhServicesHealthckDTO.setHealthChecksId(healthChecksId);
                                logger.info(centre + "中心:" + tdhServicesHealthckDTO.getType() + ": time:" + tdhServicesHealthckDTO.getLastCheck());

                                if ("".equals(lastCheck)) {
                                    lastCheck = tdhServicesHealthckDTO.getLastCheck();
                                }
                                if ("VITAL_SIGN_CHECK".equals(tdhServicesHealthckDTO.getType())) {
                                    lastCheck = tdhServicesHealthckDTO.getLastCheck();
                                }
                                tdhServicesInfoDTO.setLastCheck(lastCheck);
                                logger.info(tdhServicesHealthckDTO.toString());
                            }
                        }
                        // 插入服务名称
                        setTableName(tdhServicesInfoDTO);
//                        if("SLIPSTREAM".equals(tdhServicesInfoDTO.getType())){
//                            // 查表“流目前登记的状态”查看流服务的状态state1
//                            // 判断状态是否是healthy，如果不是，则修改state1的状态为当前状态（若state1的状态与之相同，不修改）；如果是，则判断state1的状态，如果是healthy
//                            // 则不做操作，如果不是healthy，则修改state1的状态为healthy，且取出当前时间作为stopTime，查表“tdh_services_slipstream”以当前时间为底线，向上
//                            // 获取第一个状态为healthy的时间作为startTime，如果stopTime-startTime>2.4天（可配置），则将stopTime，startTime并把hbase的对应所有表名（或“ALL”）
//                            // 存入到“应该数据同步”的表中，并作状态“未同步”
//                        }

                    });

                }

                /** 插入未知状态数据 */
                List<TdhServicesInfoDTO> resultType = new ArrayList<>();
                if(unknowNameList != null && unknowNameList.size()>0){
                   this.unknowTypeList(resultType,centre,now,nowDate,unknowNameList,tdhServicesUnknowNameListDTO,resultVO);
                }

                if(result != null && result.size()>0){
                    tdhServicesListDTO.setTdhServicesInfoDTOList(result);
                    resultVO = daoClient.saveThdServicesListData(tdhServicesListDTO);
                    logger.info("resultVO=" + resultVO);
                }
                // 返回值
                if ( !"000000".equals(resultVO.getCode())) {
                    logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                }

            }else {
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
        String name = tdhServicesInfoDTO.getName();
        logger.info("++++++++++++++++++++++++++++++name=" + name);
        if ("A".equals(centre)) {
            if (name == null || "".equals(name)) {
                tdhServicesInfoDTO.setTableName("tdha_services_tos");
            } else{
                tdhServicesInfoDTO.setTableName("tdha_services_"+name);
            }
        }else if("B".equals(centre)){
            if (name == null || "".equals(name)) {
                tdhServicesInfoDTO.setTableName("tdhb_services_tos");
            } else{
                tdhServicesInfoDTO.setTableName("tdhb_services_"+name);
            }
        }
        logger.info("---------------------name=" + tdhServicesInfoDTO.getTableName());
    }

//    public void setTableName(TdhServicesInfoDTO tdhServicesInfoDTO){
//        String centre = tdhServicesInfoDTO.getCentre();
//        String type = tdhServicesInfoDTO.getType();
//        logger.info("++++++++++++++++++++++++++++++type=" + type);
//        if ("A".equals(centre)) {
//            if (type == null || "".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_tos");
//            } else if ("TOS".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_tos");
//            } else if ("LICENSE_SERVICE".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_license");
//            } else if ("ZOOKEEPER".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_zookeeper");
//            } else if ("KAFKA".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_kafka");
//            } else if ("SEARCH".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_search");
//            } else if ("MILANO".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_milano");
//            } else if ("HDFS".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_hdfs");
//            } else if ("YARN".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_yarn");
//            } else if ("TXSQL".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_txsql");
//            } else if ("INCEPTOR".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_inceptor");
//            } else if ("SLIPSTREAM".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_slipstream");
//            } else if ("HYPERBASE".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_hyperbase");
//            } else if ("PILOT".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_pilot");
//            } else if ("TRANSPORTER".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_transporter");
//            } else if ("WORKFLOW".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_workflow");
//            } else if ("GUARDIAN".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_guardian");
//            } else if ("SLIPSTREAM_STUDIO".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_slipstream_studio");
//            }else if ("NOTIFICATION".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_notification");
//            }else if ("RUBIK".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdha_services_rubik");
//            }
//        }else if("B".equals(centre)){
//            if (type == null || "".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_tos");
//            } else if ("TOS".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_tos");
//            } else if ("LICENSE_SERVICE".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_license");
//            } else if ("ZOOKEEPER".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_zookeeper");
//            } else if ("KAFKA".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_kafka");
//            } else if ("SEARCH".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_search");
//            } else if ("MILANO".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_milano");
//            } else if ("HDFS".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_hdfs");
//            } else if ("YARN".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_yarn");
//            } else if ("TXSQL".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_txsql");
//            } else if ("INCEPTOR".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_inceptor");
//            } else if ("SLIPSTREAM".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_slipstream");
//            } else if ("HYPERBASE".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_hyperbase");
//            } else if ("PILOT".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_pilot");
//            } else if ("TRANSPORTER".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_transporter");
//            } else if ("WORKFLOW".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_workflow");
//            } else if ("GUARDIAN".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_guardian");
//            }else if ("SLIPSTREAM_STUDIO".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_slipstream_studio");
//            }else if ("NOTIFICATION".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_notification");
//            }else if ("RUBIK".equals(type)) {
//                tdhServicesInfoDTO.setTableName("tdhb_services_rubik");
//            }
//        }
//    }
    @Async("transwarpExecutor")
    public void taskSaveThdServicesJobErrorData(String joburla, String centre, String jobsize, Date now) {

        logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
        Connection client = new Connection(joburla,centre);
        List<TdhServicesJobDTO> result = new ArrayList<TdhServicesJobDTO>();
        List<TdhServicesJobVO> tdhServicesJobVOList = new ArrayList<TdhServicesJobVO>();

        Gson gs = new Gson();
        try {
            //return url+"-"+centre+"-"+username+"-"+password;
            try {
                String jobsInfo = InceptorApi.getjobs(client,0,null,"running",null);
//                System.out.println(jobsInfo);
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
            TdhServicesJobDTO tdhServicesJobDTO1 = new TdhServicesJobDTO();
            tdhServicesJobDTO1.setCentreTableName(centreJobTableName);
            if (result.size() == 0) {
                logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_NOFOUND+":result.size():"+result.size());
//                throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND.getCode(), centre + "中心异常:" + ResultExceptEnum.ERROR_NOFOUND.getMessage());
                tdhServicesJobDTO1.setCreattime(now);
                tdhServicesJobDTO1.setStatus("down");
                result.add(tdhServicesJobDTO1);
                ResultVO resultVO = daoClient.updateThdServicesjobListData(result);
                if ("000000".equals(resultVO.getCode())) {
                    logger.info(centre + "中心,修改完成:"+resultVO.getData().toString());
                    return;
                } else {
                    logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
                }
            }

            Iterator<TdhServicesJobDTO> tdhServicesJobIter = result.iterator();
            while(tdhServicesJobIter.hasNext()){
                TdhServicesJobDTO tdhServicesJobDTO = tdhServicesJobIter.next();
                String description = tdhServicesJobDTO.getDescription();
                logger.info("insert.indexof:"+description.indexOf("insert"));
                if (description == null || description.indexOf("insert") == -1){
                    tdhServicesJobIter.remove();
                }else{
                    tdhServicesJobDTO.setCentre(centre);//与URL匹配，设置中心
                    tdhServicesJobDTO.setCentreTableName(centreJobTableName);
                    tdhServicesJobDTO.setCreattime(now);
                    tdhServicesJobDTO.setHealthtime(now);
                    int index2 = description.indexOf("_",description.indexOf("_")+1);
                    String tablename = description.substring(index2-10,index2+1);
                    tdhServicesJobDTO.setTableName(tablename);
                    logger.info(tdhServicesJobDTO.toString());
                }
            }

            //1.查询job表所有job信息，若为空,则判断result的list.size,若size等于规定的数，将所有的result插入数据库，如小于则不做操作。
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
                    logger.info("jobsize:"+jobsize);
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
                    List<String> idlist = new ArrayList<>();
                    //修改数据库数据
                    boolean flag = true;
                    for (TdhServicesJobVO tdhServicesJobVO : tdhServicesJobVOList) {
                        flag = true;
                        for (TdhServicesJobDTO tdhServicesJobDTO : result) {
                            if (tdhServicesJobDTO.getTableName().equals(tdhServicesJobVO.getTableName())){
                                tdhServicesJobDTO.setId(tdhServicesJobVO.getId());
                                flag = false;
                            }
                        }
                        if (flag){
                            idlist.add(tdhServicesJobVO.getId());
                        }
                    }
                    for (String id : idlist){
                        for (TdhServicesJobVO tdhServicesJobVO : tdhServicesJobVOList) {
                            if (id.equals(tdhServicesJobVO.getId())){
                                TdhServicesJobDTO tdhServicesJobDTO = new TdhServicesJobDTO();
                                tdhServicesJobDTO.setId(id);
                                tdhServicesJobDTO.setCentre(centre);//与URL匹配，设置中心
                                tdhServicesJobDTO.setCentreTableName(centreJobTableName);
                                tdhServicesJobDTO.setCreattime(now);
                                tdhServicesJobDTO.setTableName(tdhServicesJobVO.getTableName());
                                tdhServicesJobDTO.setStatus("down");
                                result.add(tdhServicesJobDTO);
                            }
                        }
                    }
                    idlist.clear();
                    idlist = null;
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


////    测试使用
//    @Async("transwarpExecutor")
//    public void taskSaveThdServicesJobErrorData(String joburla, String centre, String jobsize, Date now) {
//                logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
//        Connection client = new Connection(joburla,centre);
//        List<TdhServicesJobDTO> result = new ArrayList<TdhServicesJobDTO>();
//        List<TdhServicesJobVO> tdhServicesJobVOList = new ArrayList<TdhServicesJobVO>();
//        DateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        List<String> date = new ArrayList<String>();
////        date.add("2019-05-06 12:25:37");
////        date.add("2019-05-10 12:25:37");
//        date.add("2019-04-26 12:25:37");
//
//        try {
//            for (int i = 0; i < 1; i++) {
//                TdhServicesJobDTO tdhServicesJobDTO = new TdhServicesJobDTO();
//                tdhServicesJobDTO.setTableName("testds" + i);
//                tdhServicesJobDTO.setStatus("running");
//                result.add(tdhServicesJobDTO);
//            }
//            for (int i = 0; i < 1; i++) {
//                TdhServicesJobVO tdhServicesJobVO = new TdhServicesJobVO();
//                tdhServicesJobVO.setTableName("testds" + i);
//                tdhServicesJobVO.setStatus("down");
//                tdhServicesJobVO.setCreattime(format2.parse(date.get(i)));
//                tdhServicesJobVO.setHealthtime(format2.parse(date.get(i)));
//                tdhServicesJobVOList.add(tdhServicesJobVO);
//            }
////            TdhServicesJobDTO tdhServicesJobDTO = new TdhServicesJobDTO();
////                tdhServicesJobDTO.setTableName("testds0");
////                result.add(tdhServicesJobDTO);
////            TdhServicesJobVO tdhServicesJobVO = new TdhServicesJobVO();
////                tdhServicesJobVO.setTableName("testds0");
////                tdhServicesJobVO.setHealthtime(format2.parse(date.get(0)));
////                tdhServicesJobVOList.add(tdhServicesJobVO);
//            serviceDsThread.taskSaveDsData(tdhServicesJobVOList, result, "tdha_ds_info", "a", now.getTime(), now);
//        }catch (Exception e){
//            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, centre + "中心异常:" );
//        }
//
//    }

    @Async("transwarpExecutor")
    public void taskSaveThdUsersListDataThread(String url, String centre, String username, String password) {

        logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
        Connection client = new Connection(url,centre);
        List<UserDTO> result = new ArrayList<UserDTO>();
        Date now = new Date();
        Gson gs = new Gson();
        try {
            //return url+"-"+centre+"-"+username+"-"+password;
            if (UserApi.login(client, username, password)) {
                try {
                    String usersInfo = UserApi.getAllUsers1(client);
                    System.out.println(usersInfo);
                    result = gs.fromJson(usersInfo, new TypeToken<List<UserDTO>>() {
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
                //插入数据库，调用monitor-dao微服务
                ResultVO resultVO = daoClient.saveUserInfo(result);
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

    public void taskUpdateThdDsData(String centre) {
        logger.info(centre + "---------------------进入线程" + Thread.currentThread().getName() + " 执行异步任务：");
        ResultVO resultVO = daoClient.updateThdDsData(centre);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            // return resultVO.getData().toString();
        } else {
            logger.info(centre + "中心异常：e=" + ResultExceptEnum.ERROR_UPDATE + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, centre + "中心异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }


    private List<TdhServicesInfoDTO> healthChecksIdNumIsEmpty(List<TdhServicesInfoDTO> result,TdhServicesListDTO tdhServicesListDTO,String centre,
                                                                 Date now,String nowDate,TdhServicesAVO thdServicesInfoNow,String healthChecksIdNum,TdhServicesListDTO tdhServicesUnknowNameListDTO, ResultVO resultVO){

        result.forEach(tdhServicesInfoDTO -> {
            // 与URL匹配，设置中心，初始化数据
            tdhServicesInfoDTO.setCentre(centre);
            tdhServicesInfoDTO.setCreattime(now);


            tdhServicesInfoDTO.setTaskTime(nowDate);
            // 计数器
            tdhServicesInfoDTO.setHealthChecksId("1");
            // 确定表名
            if (centre.equals(centrea)){
                tdhServicesInfoDTO.setTableName(TDHA_SERVICES_INFO_NOW);
            }
            if (centre.equals(centreb)){
                tdhServicesInfoDTO.setTableName(TDHB_SERVICES_INFO_NOW);
            }

//            logger.info(tdhServicesInfoDTO.toString());
            logger.info("中心："+centre+"，tdhServicesInfoDTO.getType():"+tdhServicesInfoDTO.getType());
            logger.info("中心："+centre+"，tdhServicesInfoDTO.getName():"+tdhServicesInfoDTO.getName());
        });
        // 服务返回个数不正确，抛异常
        Integer nameNum = result.size();
        logger.info("typeNum.equals(nameNumA):"+String.valueOf(nameNum.equals(nameNumA)));
//        logger.info("typeNum==(typeNumA):"+String.valueOf(typeNum== (Integer.valueOf(typeNumA).intValue())));

        // 服务A个数
        if((centre.equals(centrea) && nameNum!= (Integer.valueOf(nameNumA).intValue()))){
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常,返回个数不正确：内容为："+"nameNum:"+nameNum+",nameNumA:"+nameNumA+","+result.toString());
        }
        // 服务B个数
        if(centre.equals(centreb) && nameNum!= (Integer.valueOf(nameNumB).intValue())){
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常,返回个数不正确：内容为："+"nameNum:"+nameNum+",nameNumB:"+nameNumB+","+result.toString());
        }

        resultVO = daoClient.saveThdServicesInfoNowListData(tdhServicesListDTO);
        logger.info("初始化服务 实时数据保存结果 resultVO=" + resultVO);

        // 判断返回值，如果不成功直接抛异常，不进行所有数据保存操作
        if(!"000000".equals(resultVO.getCode())){
            logger.info(centre + "中心异常,添加初始化信息失败：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常,添加初始化信息失败：" + resultVO.getMsg()+resultVO.getData());
        }
        return result;
    }

    private List<TdhServicesInfoDTO> healthChecksIdNumNotIsEmpty(List<TdhServicesInfoDTO> result,TdhServicesListDTO tdhServicesListDTO,String centre,
                      Date now,String nowDate,TdhServicesAVO thdServicesInfoNow,String healthChecksIdNum,ArrayList<String> unknowTypeList,TdhServicesListDTO tdhServicesUnknowTypeListDTO, ResultVO resultVO){

        // 更新所有未知状态类型信息
        result.forEach(tdhServicesInfoDTO -> {
            // 修改实时数据  程序计数器 +1，修改数据
            tdhServicesInfoDTO.setCentre(centre);
            tdhServicesInfoDTO.setCreattime(now);
            tdhServicesInfoDTO.setTaskTime(nowDate);

            // 判断是否达到180s,如果达到180，计数器重新修改为1
            if(Integer.parseInt(thdServicesInfoNow.getHealthChecksId()) == NUM){
                // 计数器
                tdhServicesInfoDTO.setHealthChecksId("1");
            }else {
                // 计数器
                tdhServicesInfoDTO.setHealthChecksId(String.valueOf(Integer.valueOf(healthChecksIdNum)+1));
            }
            // 确定表名
            if (centre.equals(centrea)){
                tdhServicesInfoDTO.setTableName(TDHA_SERVICES_INFO_NOW);
            }

            if (centre.equals(centreb)){
                tdhServicesInfoDTO.setTableName(TDHB_SERVICES_INFO_NOW);
            }
            logger.info(tdhServicesInfoDTO.toString());
        });

        List<TdhServicesInfoDTO> resultType = new ArrayList<>();
        if(unknowTypeList != null && unknowTypeList.size()>0){
            TdhServicesInfoDTO tdhServicesInfoDTO = new TdhServicesInfoDTO();
            // 循环list获取type值
            unknowTypeList.forEach(type -> {
                tdhServicesInfoDTO.setType(type);
                tdhServicesInfoDTO.setHealth(UNKNOW);
                tdhServicesInfoDTO.setTaskTime(nowDate);
                resultType.add(tdhServicesInfoDTO);
            });

            // 更新未知状态数据
            tdhServicesUnknowTypeListDTO.setTdhServicesInfoDTOList(resultType);
            resultVO = daoClient.updateThdServicesInfoNow(tdhServicesUnknowTypeListDTO, healthChecksIdNum);
            logger.info("resultVO=" + resultVO);

            // 判断返回值，如果不成功直接抛异常，不进行所有数据保存操作
            if(!"000000".equals(resultVO.getCode())){
                logger.info(centre + "中心异常,任务计数器数值前后不同，不进行所有数据保存操作：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
                throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常,任务计数器数值前后不同，不进行所有数据保存操作:" + resultVO.getMsg()+resultVO.getData());
            }
        }
        // 更新实时数据
        tdhServicesListDTO.setTdhServicesInfoDTOList(result);
        resultVO = daoClient.updateThdServicesInfoNow(tdhServicesListDTO, healthChecksIdNum);
        logger.info("resultVO=" + resultVO);

        // 判断返回值，如果不成功直接抛异常，不进行所有数据保存操作
        if(!"000000".equals(resultVO.getCode())){
            logger.info(centre + "中心异常,任务计数器数值前后不同，不进行所有数据保存操作：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常,任务计数器数值前后不同，不进行所有数据保存操作:" + resultVO.getMsg()+resultVO.getData());
        }

        return result;

    }

    /**
     * @author Crayon
     * @Description 插入未知状态数据
     * @date 2019/4/24 4:59 PM
     * @params [resultType, centre, now, nowDate, unknowTypeList, tdhServicesUnknowTypeListDTO]
     * @exception
     * @return void
     */
    private void unknowTypeList(List<TdhServicesInfoDTO> resultType,String centre,
                                Date now,String nowDate,ArrayList<String> unknowTypeList,TdhServicesListDTO tdhServicesUnknowTypeListDTO,ResultVO resultVO){
        TdhServicesInfoDTO tdhServicesInfoDTO = new TdhServicesInfoDTO();
        // 循环list获取type值
        unknowTypeList.forEach(type -> {
            tdhServicesInfoDTO.setCentre(centre);
            String healthChecksId = KeyUtil.genUniqueKey();
            tdhServicesInfoDTO.setCreattime(now);
            tdhServicesInfoDTO.setTaskTime(nowDate);
            tdhServicesInfoDTO.setHealthChecksId(healthChecksId);
            tdhServicesInfoDTO.setTableName("tdha_services_hdfs");
            tdhServicesInfoDTO.setType(type);
            tdhServicesInfoDTO.setHealth(UNKNOW);
            logger.info(tdhServicesInfoDTO.toString());
            // 插入服务名称
            setTableName(tdhServicesInfoDTO);
            resultType.add(tdhServicesInfoDTO);
        });

        // 更新未知状态数据
        tdhServicesUnknowTypeListDTO.setTdhServicesInfoDTOList(resultType);
        resultVO = daoClient.saveThdServicesListData(tdhServicesUnknowTypeListDTO);
        logger.info("resultVO=" + resultVO);

        // 判断返回值，如果不成功直接抛异常，不进行所有数据保存操作
        if(!"000000".equals(resultVO.getCode())){
            logger.info(centre + "中心异常,任务计数器数值前后不同，不进行所有数据保存操作：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, centre + "中心异常,任务计数器数值前后不同，不进行所有数据保存操作:" + resultVO.getMsg()+resultVO.getData());
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
