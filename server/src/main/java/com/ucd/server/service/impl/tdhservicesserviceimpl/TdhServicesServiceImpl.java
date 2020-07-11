package com.ucd.server.service.impl.tdhservicesserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.server.exception.SoftwareException;

import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.service.impl.ServiceThread;
import com.ucd.server.service.tdhservicesservice.TdhServicesService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TdhServicesServiceImpl implements TdhServicesService {

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
    @Autowired
    public DaoClient daoClient;


    private final String TDHA_SERVICES_INFO_NOW = "tdha_services_info_now";


    private final String TDHB_SERVICES_INFO_NOW = "tdhb_services_info_now";

//    @Autowired
//    public LocalDaoClient localDaoClient;

    @Autowired
    public TdhTaskParameterMapper tdhTaskParameterMapper;

    @Autowired
    public ServiceThread serviceThread;
    private final static Logger logger = LoggerFactory.getLogger(TdhServicesServiceImpl.class);
//    @Override
//    public List<TdhServicesInfoDTO> saveThdServicesData() throws Exception{
//        List<TdhServicesInfoDTO> result = new ArrayList<>();
//        Connection client = new Connection("http://10.28.3.51:8180/api");
//        Date now = new Date();
//            Gson gs = new Gson();
//            if (UserApi.login(client, "admin", "admin")) {
//                String servicesInfo = ServicesApi.getAllServices(client);
//                System.out.println(servicesInfo);
//                result = gs.fromJson(servicesInfo,new TypeToken<List<TdhServicesInfoDTO>>(){}.getType());
//                logger.info(result.toString());
//                UserApi.logout(client);
//                client.close();
//                if (result == null) {
//                    logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND);
//                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND);
//                }
//                for (TdhServicesInfoDTO tdhServicesInfoDTO:result){
//                    String healthChecksId = KeyUtil.genUniqueKey();
//                    tdhServicesInfoDTO.setCreattime(now);
//                    tdhServicesInfoDTO.setHealthChecksId(healthChecksId);
//                    logger.info(tdhServicesInfoDTO.toString());
//                    List<TdhServicesHealthckDTO> tdhServicesHealthckDTOList = tdhServicesInfoDTO.getHealthChecks();
//                    if (null == tdhServicesHealthckDTOList || tdhServicesHealthckDTOList.size() ==0){
//                        logger.info("tdhServicesHealthckDTOList为空");
//                    }else{
//                        for (TdhServicesHealthckDTO tdhServicesHealthckDTO:tdhServicesHealthckDTOList){
//                            tdhServicesHealthckDTO.setCreattime(now);
//                            tdhServicesHealthckDTO.setHealthChecksId(healthChecksId);
//                            logger.info(tdhServicesHealthckDTO.toString());
//                        }
//                    }
//                    //插入数据库，调用monitor-dao微服务
//                    ResultVO resultVO = daoClient.saveThdServicesData(tdhServicesInfoDTO);
//                    logger.info("resultVO=" + resultVO);
//                    if("2"== resultVO.getCode()){
//                        logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg());
//                        throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,resultVO.getMsg());
//                    }else if ("1"== resultVO.getCode()){
//                        logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg());
//                        throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,resultVO.getMsg());
//                    }
//                }
//
//            }
//
//        return result;
//
//    }

    @Override
    public String saveThdServicesListData() throws Exception {
        //return urla+"-"+centrea+"-"+usernamea+"-"+passworda+"==="+urlb+"-"+centreb+"-"+usernameb+"-"+passwordb;
        int num = tdhTaskParameterMapper.updateTdhServiceTaskState(1);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = sdf.format(now).substring(0, 18) + "0";
        if (num == 1) {
            serviceThread.saveThdServicesListDataThread(serviceinfourla, centrea, usernamea, passworda, nowDate);
            serviceThread.saveThdServicesListDataThread(serviceinfourlb, centreb, usernameb, passwordb, nowDate);
        } else {
            return "30";
        }
        return "30";


    }

    @Override
    public PageView getThdServicesInfo(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) throws Exception {
        if (tdhServicesInfoDTO.getName() == null || "".equals(tdhServicesInfoDTO.getName())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",name名称不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "name名称不能为空");
        }
        if (tdhServicesInfoDTO.getCentre() == null || "".equals(tdhServicesInfoDTO.getCentre())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "centre中心不能为空");
        }
        serviceThread.setTableName(tdhServicesInfoDTO);
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView", pageView);
        models.put("tdhServicesInfoDTO", tdhServicesInfoDTO);
        ResultVO resultVO = daoClient.getThdServicesInfo(models);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            Object object = resultVO.getData();
            if (object != null) {
                String pageViewString = Tools.toJson(object);
                Gson gs = new Gson();
                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
                }.getType());
                logger.info("pageViewString:" + pageViewString);
            }
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg() + resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, resultVO.getMsg() + resultVO.getData());
        }
        return pageView;
    }

    @Override
    @Transactional
    public String updateDataSynchronizationState(String centre) throws Exception {
        return null;
    }

    @Override
    public PageView getThdServicesListNow(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) throws Exception {

        if (ObjectUtils.isEmpty(tdhServicesInfoDTO.getCentre())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "centre中心不能为空");
        }
        // 确定表名
        if (tdhServicesInfoDTO.getCentre().equals(centrea)) {
            tdhServicesInfoDTO.setTableName(TDHA_SERVICES_INFO_NOW);
        }

        if (tdhServicesInfoDTO.getCentre().equals(centreb)) {
            tdhServicesInfoDTO.setTableName(TDHB_SERVICES_INFO_NOW);
        }
        logger.info(tdhServicesInfoDTO.toString());
        Map<String, Object> models = new HashMap<>(16);
        models.put("pageView", pageView);
        models.put("tdhServicesInfoDTO", tdhServicesInfoDTO);
        ResultVO resultVO = daoClient.getThdServicesListNow(models);
        logger.info("resultVO=" + resultVO);

        pageView = this.commonResult(pageView, resultVO);

        return pageView;
    }


    @Override
    public PageView getTdhHealthStatus(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) throws Exception {
        if (ObjectUtils.isEmpty(tdhServicesInfoDTO.getTaskTimeStart()) || ObjectUtils.isEmpty(tdhServicesInfoDTO.getTaskTimeEnd()) || ObjectUtils.isEmpty(tdhServicesInfoDTO.getSecond())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",参数不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "参数不能为空");
        }

        if (ObjectUtils.isEmpty(tdhServicesInfoDTO.getCentre())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "centre中心不能为空");
        }

        // 确定表名
        ServiceThread serviceThread = new ServiceThread();
        serviceThread.setTableName(tdhServicesInfoDTO);

        // 时间处理java8
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime taskTimeStart1 = LocalDateTime.parse(tdhServicesInfoDTO.getTaskTimeStart(), dtf);
        LocalDateTime taskTimeEnd1 = LocalDateTime.parse(tdhServicesInfoDTO.getTaskTimeEnd(), dtf);

   /*    Date newDate = java.sql.Date.valueOf(String.valueOf(taskTimeStart1));
       Date newDate1 = java.sql.Date.valueOf(String.valueOf(taskTimeEnd1));
*/
        // 时间处理，秒数为00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date taskTimeStart = sdf.parse(tdhServicesInfoDTO.getTaskTimeStart());
        Date taskTimeEnd = sdf.parse(tdhServicesInfoDTO.getTaskTimeEnd());

        Calendar cal = Calendar.getInstance();
        cal.setTime(taskTimeStart);
        cal.set(Calendar.SECOND, 0);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(taskTimeEnd);
        cal1.set(Calendar.SECOND, 0);

        tdhServicesInfoDTO.setTaskTimeStart(sdf.format(cal.getTime()));
        tdhServicesInfoDTO.setTaskTimeEnd(sdf.format(cal1.getTime()));
        Map<String, Object> model = new HashMap<>(16);

        model.put("pageView", pageView);
        model.put("tdhServicesInfoDTO", tdhServicesInfoDTO);

        ResultVO resultVO = daoClient.getTdhHealthStatusByTime(model);

        pageView = this.commonResult(pageView, resultVO);

        logger.info("resultVO=" + resultVO);

        return pageView;
    }


    public PageView commonResult(PageView pageView, ResultVO resultVO) {
        if ("000000".equals(resultVO.getCode())) {
            Object object = resultVO.getData();
            if (object != null) {
                String pageViewString = Tools.toJson(object);
                Gson gs = new Gson();
                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
                }.getType());
                logger.info("pageViewString:" + pageViewString);
            }
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg() + resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, resultVO.getMsg() + resultVO.getData());
        }
        return pageView;
    }


//    @Override
//    public List<com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO> saveThdServicesLocalDao() throws Exception {
//        List<com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO> result = new ArrayList<>();
//        Connection client = new Connection("http://10.28.3.51:8180/api");
//        Date now = new Date();
//        Gson gs = new Gson();
//        if (UserApi.login(client, "admin", "admin")) {
//            String servicesInfo = ServicesApi.getAllServices(client);
//            System.out.println(servicesInfo);
//            result = gs.fromJson(servicesInfo,new TypeToken<List<com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO>>(){}.getType());
//            logger.info(result.toString());
//            UserApi.logout(client);
//            client.close();
//            if (result == null) {
//                logger.info("异常：e=" + ResultExceptEnum.ERROR_NOFOUND);
//                throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND);
//            }
//            for (com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO tdhServicesInfoDTO:result){
//                String healthChecksId = KeyUtil.genUniqueKey();
//                tdhServicesInfoDTO.setCreattime(now);
//                tdhServicesInfoDTO.setHealthChecksId(healthChecksId);
//                logger.info(tdhServicesInfoDTO.toString());
//                List<com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO> tdhServicesHealthckDTOList = tdhServicesInfoDTO.getHealthChecks();
//                if (null == tdhServicesHealthckDTOList || tdhServicesHealthckDTOList.size() ==0){
//                    logger.info("tdhServicesHealthckDTOList为空");
//                }else{
//                    for (com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO tdhServicesHealthckDTO:tdhServicesHealthckDTOList){
//                        tdhServicesHealthckDTO.setCreattime(now);
//                        tdhServicesHealthckDTO.setHealthChecksId(healthChecksId);
//                        logger.info(tdhServicesHealthckDTO.toString());
//                    }
//                }
//                //插入数据库，调用monitor-dao微服务
//                ResultVO resultVO = localDaoClient.saveThdServicesData(tdhServicesInfoDTO);
//                logger.info("resultVO=" + resultVO);
//                if("2"== resultVO.getCode()){
//                    logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg());
//                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,resultVO.getMsg());
//                }else if ("1"== resultVO.getCode()){
//                    logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg());
//                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,resultVO.getMsg());
//                }
//            }
//
//        }
//
//        return result;
//    }
}
