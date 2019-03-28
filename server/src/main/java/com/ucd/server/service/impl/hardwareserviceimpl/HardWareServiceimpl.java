package com.ucd.server.service.impl.hardwareserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.*;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.hardwareservice.HardWareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class HardWareServiceimpl implements HardWareService {

    @Autowired
    public DaoClient daoClient;
    private final static Logger logger = LoggerFactory.getLogger(HardWareService.class);

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
        ResultVO resultVO = daoClient.saveHardWareInfo(hardwareInfoDTO);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO.getData().toString();
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,  "异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }

    @Override
    public PageView gethardwareInfo(PageView pageView, HardwareDTO hardwareDTO) throws Exception {
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("hardwareDTO",hardwareDTO);
        ResultVO resultVO = daoClient.getHardWareInfo(models);
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
}
