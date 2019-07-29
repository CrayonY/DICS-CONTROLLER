package com.ucd.server.service.impl.hardwareservice2impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.HardWareTypeEnum;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.result.ApiResultType;
import com.ucd.common.utils.StringTool;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareCpuDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareInfoDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareNowDTO;
import com.ucd.daocommon.VO.hardwareVO.*;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.hardwareservice2.HardWareService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HardWareService2impl implements HardWareService2 {

    @Autowired
    public DaoClient daoClient;

    private final String HARD_WARE_ = "hard_ware_";

    private final static Logger logger = LoggerFactory.getLogger(HardWareService2.class);

//    @Override
//    public String saveHardWareInfo(HardwareDTO hardwareDTO) throws Exception {
//        if (hardwareDTO == null || hardwareDTO.getHost() == null ||"".equals(hardwareDTO.getHost())){
//            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER);
//        }
//        hardwareDTO.setCreattime(new Date());
//        ResultVO resultVO = daoClient.saveHardWareInfo(hardwareDTO);
//        logger.info("resultVO=" + resultVO);
//        if ("000000".equals(resultVO.getCode())) {
//            return resultVO.getData().toString();
//        } else {
//            logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
//            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,  "异常:" + resultVO.getMsg()+resultVO.getData());
//        }
//    }

    @Override
    public String saveHardWareInfo(HardwareInfoDTO hardwareInfoDTO) throws Exception {
        if (hardwareInfoDTO == null || hardwareInfoDTO.getHost() == null ||"".equals(hardwareInfoDTO.getHost())){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER);
        }
        hardwareInfoDTO.setCreattime(new Date());
        ResultVO resultVO = daoClient.saveHardWareInfo2(hardwareInfoDTO);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO.getData().toString();
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,  "异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }

    @Override
    public PageView gethardwareInfo(PageView pageView, HardwareNowDTO hardwareNowDTO) throws Exception {
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("hardwareNowDTO",hardwareNowDTO);
        ResultVO resultVO = daoClient.getHardWareInfo2(models);
        logger.info("resultVO=" + resultVO);
        if("000000".equals(resultVO.getCode())){
            Object object = resultVO.getData();
            if (object != null) {
                String pageViewString = Tools.toJson(object);
                Gson gs = new Gson();
                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
                }.getType());
                logger.info("pageViewString:" + pageViewString);
            }
        }else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT,resultVO.getMsg()+resultVO.getData());
        }
        return  pageView;
    }

    @Override
    public ResultVO<Map<String, Object>> getHardWareListNow(String host){
        ResultVO resultVO;
        Map<String, Object> result = Maps.newHashMap();
        result.put("cpuDiskMem",Lists.newArrayList());
        result.put("nic",Lists.newArrayList());
        result.put("thread",Lists.newArrayList());
        try{

            // cpu、disk、mem
            resultVO = daoClient.getHardWareInfoListNow2(host);
            Object object = resultVO.getData();
            if(!ObjectUtils.isEmpty(object)){
                String hardWareInfoNowList = Tools.toJson(object);
                // 转成list
                List<HardwareNowVO> hardwareNowVOList = JSON.parseArray(hardWareInfoNowList,HardwareNowVO.class);
                result.put("cpuDiskMem",object);
            }

            // nic
            resultVO = daoClient.getHardWareNicNow2(host);
            Object objectNic = resultVO.getData();
            if(!ObjectUtils.isEmpty(objectNic)){
                result.put("nic",objectNic);

            }

            // 进程
            resultVO = daoClient.getHardWareThreadNow2(host);
            Object objectPid = resultVO.getData();
            if(!ObjectUtils.isEmpty(objectNic)){
                result.put("thread",objectPid);
            }
            return ResultVO.SUCC(ApiResultType.SUCCESS.code,ApiResultType.SUCCESS.message,result);
        }catch (Exception e){
            logger.error("硬件进行查询异常：", e);
            return ResultVO.FAIL(result).initErrCodeAndMsg(ApiResultType.SYS_ERROR.code,
                    ApiResultType.SYS_ERROR.message);
        }
    }

    @Override
    public ResultVO<Map<String, Object>> getHardWareStatusByTime(String type,String nipsOrThreadNames,HardwareCpuDTO hardwareCpuDTO) {
        ResultVO resultVO;
        Map<String, Object> result = Maps.newHashMap();
        try{
             if(ObjectUtils.isEmpty(hardwareCpuDTO.getSecond()) || ObjectUtils.isEmpty(type)
                     || ObjectUtils.isEmpty(hardwareCpuDTO.getChecktimeStart())
                     || ObjectUtils.isEmpty(hardwareCpuDTO.getChecktimeEnd())
                     || ObjectUtils.isEmpty(hardwareCpuDTO.getSecond())
                     || ObjectUtils.isEmpty(hardwareCpuDTO.getHost())){

                 return ResultVO.FAIL(ApiResultType.PARAMETER_ILLEGAL.code,ApiResultType.PARAMETER_ILLEGAL.message,result);
             }

            List<String> list = new ArrayList<>();
            // 判断如果类型为nic或者thread时，nipsOrThreadNames是否为空
             if(HardWareTypeEnum.NIC.getValue().equals(type) || HardWareTypeEnum.THREAD.getValue().equals(type)){
                 if (ObjectUtils.isEmpty(nipsOrThreadNames)){
                     return ResultVO.FAIL(ApiResultType.PARAMETER_ILLEGAL.code,ApiResultType.PARAMETER_ILLEGAL.message,result);
                 }

                 // 格式化处理nipsOrThreadNames字段信息
                list = StringTool.stringToStrList(nipsOrThreadNames,",");
             }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date checkTimeStart = sdf.parse(hardwareCpuDTO.getChecktimeStart());
            Date checkTimeEnd = sdf.parse(hardwareCpuDTO.getChecktimeEnd());

            // 秒数归00
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkTimeStart);
            cal.set(Calendar.SECOND, 0);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(checkTimeEnd);
            cal1.set(Calendar.SECOND, 0);

            hardwareCpuDTO.setChecktimeStart(sdf.format(cal.getTime()));
            hardwareCpuDTO.setChecktimeEnd(sdf.format(cal1.getTime()));

            Map<String, Object> model = new HashMap<>(16);
            model.put("hardwareCpuDTO",hardwareCpuDTO);
            model.put("second",hardwareCpuDTO.getSecond());
            model.put("type",type);
            // 获取DAO层返回信息
            resultVO = daoClient.getHardWareStatusByTime2(model);

            if(!ObjectUtils.isEmpty(resultVO.getCode()) && !"000000".equals(resultVO.getCode())){
                throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, resultVO.getMsg() + resultVO.getData());
            }

            PageView pageView = new PageView();

            Object obj = resultVO.getData();
            if (obj != null) {
                String pageViewString = Tools.toJson(obj);
                Gson gs = new Gson();
                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
                }.getType());

                logger.info("pageViewString:" + pageViewString);

                // 查看硬件CPU状态信息
                if(HardWareTypeEnum.CPU.getValue().equals(type)){
                    List<HardWareCpuVO> getCpuList = pageView.getRecords();
                    result.put("hardWareList", getCpuList);
                }

                if(HardWareTypeEnum.DISK.getValue().equals(type)){
                    List<HardWareDiskVO> getDiskList = pageView.getRecords();
                    result.put("hardWareList", getDiskList);
                }

                if(HardWareTypeEnum.MEM.getValue().equals(type)){
                    List<HardWareMemVO> hardWareMemVOList = pageView.getRecords();
                    result.put("hardWareList", hardWareMemVOList);
                }


                if(HardWareTypeEnum.NIC.getValue().equals(type)){
                    List<Map<String, Object>> nicListMap = new ArrayList<Map<String, Object>>();
                    PageView finalPageView = pageView;
                    list.stream().forEach((String string) ->{

                        List<HardwareNicVO> hardwareNicVOList = finalPageView.getRecords();
                        // 转换类型
                        String ListVOString = Tools.toJson(hardwareNicVOList);
                        List<HardwareNicVO> hardwareNicVOList3 = new ArrayList<>();
                        Gson gson = new Gson();
                        // 将LinkedHashMap类型转换成可以识别的实体类
                        hardwareNicVOList3 = gson.fromJson(ListVOString, new TypeToken<List<HardwareNicVO>>() {}.getType());

                        // 对数据进行筛选
                        List<HardwareNicVO> hardwareNicVOList1 = hardwareNicVOList3.stream().filter(a -> a.getNip().equals(string)).collect(Collectors.toList());
                        Map<String, Object> nicMap = Maps.newHashMap();
                        nicMap.put("ipName",string);
                        nicMap.put("List",hardwareNicVOList1);
                        nicListMap.add(nicMap);
                    });
                    result.put("nicip", nicListMap);
                }


                // 进程数据组装格式化
                if(HardWareTypeEnum.THREAD.getValue().equals(type)){
                    PageView finalPageView = pageView;
                    list.stream().forEach((String string) ->{
                        List<HardwareThreadVO> hardwareThreadVOList = finalPageView.getRecords();
                        // 转换类型
                        String ListVOString = Tools.toJson(hardwareThreadVOList);

                        List<HardwareThreadVO> hardwareThreadVOList3 = new ArrayList<>();
                        Gson gson = new Gson();
                        // 将LinkedHashMap类型转换成可以识别的实体类
                        hardwareThreadVOList3 = gson.fromJson(ListVOString, new TypeToken<List<HardwareThreadVO>>() {}.getType());
                        // 对数据进行筛选
                        hardwareThreadVOList3 = hardwareThreadVOList3.stream().filter(a -> a.getPidname().equals(string)).collect(Collectors.toList());
                        result.put(string, hardwareThreadVOList3);
                    });
                }
            }
            return ResultVO.SUCC(ApiResultType.SUCCESS.code,ApiResultType.SUCCESS.message,result);

        }catch (Exception e){
            logger.error("硬件进行查询异常：", e);
            return ResultVO.FAIL(ApiResultType.SYS_ERROR.code,
                    ApiResultType.SYS_ERROR.message,result);

        }
    }

    @Override
    public ResultVO getHardWareHostList() throws Exception {
        ResultVO resultVO = daoClient.getHardWareHostList2();
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO;
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,  "异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }


//    @Override
//    public PageView gethardwareInfo(PageView pageView, HardwareDTO hardwareDTO) throws Exception {
//        Map<String, Object> models = new HashMap<String, Object>();
//        models.put("pageView",pageView);
//        models.put("hardwareDTO",hardwareDTO);
//        ResultVO resultVO = daoClient.getHardWareInfo(models);
//        logger.info("resultVO=" + resultVO);
//        if("000000".equals(resultVO.getCode())){
//            Object object = resultVO.getData();
//            if (object != null) {
//                String pageViewString = Tools.toJson(object);
//                Gson gs = new Gson();
//                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
//                }.getType());
//                logger.info("pageViewString:" + pageViewString);
//            }
//        }else {
//            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg()+resultVO.getData());
//            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT,resultVO.getMsg()+resultVO.getData());
//        }
//        return  pageView;
//    }
}
