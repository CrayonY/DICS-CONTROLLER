package com.ucd.server.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.UUIDUtils;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.tdhdsListDTO.TdhDsListDTO;
import com.ucd.daocommon.VO.tdhdsVO.TdhDsVO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesJobVO;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.utils.ForFile;
import com.ucd.server.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: ServiceDsThread
 * @Description: TODO
 * @Author: gongweimin
 * @CreateDate: 2019/5/5 12:36
 * @Version 1.0
 * @Copyright: Copyright2018- BJCJ Inc. All rights reserved.
 **/
@Component
public class FileDsThread {
    @Value("${basicparameters.transwarp.guardian-access-token}")
    public String guardianAccessToken;

    @Autowired
    public DaoClient daoClient;
    @Autowired
    public ServiceSync serviceSync;

    private final static Logger logger = LoggerFactory.getLogger(FileDsThread.class);

    @Async("transwarpExecutor")
    public void taskSaveDsData(StringBuffer filetext, List<TdhDsDTO> tdhDsDTOS, String userCode, String urlotherside) {
        Gson gs = new Gson();
        logger.info("---------------------进入线程" + Thread.currentThread().getName() + " StringBuffer:" + filetext);
        try {
            //本地生成文件
            boolean creatFileFlag = ForFile.createFile("testFile", filetext.toString());
            if (!creatFileFlag) {
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + tdhDsDTOS.get(0).getCentre() + "中心生成文件异常：");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, tdhDsDTOS.get(0).getCentre() + "中心参数异常");
            }
            //删除原有文件上传文件
            Thread.sleep(2000);
//            boolean tdhdeleteFileFlag = ForFile.TDHdelete("testFile",guardianAccessToken);
//            if (!tdhdeleteFileFlag) {
//                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + tdhDsDTOS.get(0).getCentre() + "中心删除文件异常：");
//                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, tdhDsDTOS.get(0).getCentre() + "中心上传文件异常");
//            }
            boolean tdhcreateFileFlag = ForFile.TDHcreate("testFile", guardianAccessToken);
            if (!tdhcreateFileFlag) {
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + tdhDsDTOS.get(0).getCentre() + "中心上传文件异常：");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, tdhDsDTOS.get(0).getCentre() + "中心上传文件异常");
            }
            //调取数据同步方法
            String testFlag = "";

            testFlag = serviceSync.SyncThdListDataThread();
            if (!("OK".equals(testFlag))) {
                logger.info("数据同步shell脚本失败");
                throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "数据同步shell脚本失败!异常：:" + testFlag);
            }
        } catch (Exception e) {
            ResultVO resultDsSyncVO = new ResultVO();
            String resultDsSync = "";
            try {
                for (TdhDsDTO tdhDsDTO : tdhDsDTOS) {
                    tdhDsDTO.setState(3);
                    tdhDsDTO.setId(null);
                }
                resultDsSync = HttpClientUtils.postString(urlotherside + "/server-0.0.1-SNAPSHOT/softwareDs/updateThdDsListData", Tools.toJson(tdhDsDTOS), "application/json", null);
                resultDsSyncVO = gs.fromJson(resultDsSync, new TypeToken<ResultVO>() {
                }.getType());
            } catch (Exception e1) {
                logger.info("同步状态通知失败,接口超时等异常:" + e1);
                throw new SoftwareException(ResultExceptEnum.ERROR_HTTP, "同步状态通知失败,接口超时等异常：:" + e1);
            }
            logger.info("resultDsSyncVO:" + resultDsSyncVO);
            if ("000000".equals(resultDsSyncVO.getCode())) {
                logger.info("同步状态已通知对端");
                //修改审核表对应状态
                Map<String, Object> models = new HashMap<String, Object>();
                TdhDsListDTO tdhDssListDTO = new TdhDsListDTO();
                tdhDssListDTO.setTdhDsDTOList(tdhDsDTOS);
                models.put("syncState", 3);
                models.put("userCode", userCode);
                models.put("tdhDsDTOS", tdhDssListDTO);
                ResultVO resultVO1 = daoClient.updateTdhDsInfoS(models);
                if ("000000".equals(resultVO1.getCode())) {
                    logger.info("DS表同步状态“同步中”！修改成功");
                } else {
                    logger.info("DS表同步状态“同步中”！修改失败");
                    throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "DS表同步状态“同步中”！修改失败异常：:" + resultVO1.getMsg() + resultVO1.getData());
                }
            }
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
        System.out.println("index1:" + index1);//index1:7
        int index2 = a.indexOf("_", a.indexOf("_") + 1);
        System.out.println("index2:" + index2);//index2:32
        String b = a.substring(12, index2);
        System.out.println("b:" + b);//b:station_c2222456sdfg
        Date now = new Date();
        long t1 = now.getTime();
        System.out.println("t1:" + t1);//
        System.out.println(a.indexOf("insert"));//0
    }
}
