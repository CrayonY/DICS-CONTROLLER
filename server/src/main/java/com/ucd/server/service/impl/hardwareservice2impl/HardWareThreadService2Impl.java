package com.ucd.server.service.impl.hardwareservice2impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareThreadDTO;
import com.ucd.server.enums.SoftwareExceptEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.hardwareservice2.HardWareThreadService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwm on 2019/4/1.
 */
@Service
public class HardWareThreadService2Impl implements HardWareThreadService2 {

    @Autowired
    public DaoClient daoClient;

    private final static Logger logger = LoggerFactory.getLogger(HardWareThreadService2.class);


    @Override
    public PageView getHardWareThread(PageView pageView, HardwareThreadDTO hardwareThreadDTO) throws Exception {
        Map<String, Object> models = new HashMap<String, Object>();
        logger.info("hardwareThreadDTO:" + hardwareThreadDTO);
        if (hardwareThreadDTO.getTablename() == null || "".equals(hardwareThreadDTO.getTablename())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",表名不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "表名不能为空");
        }
        if ("nowtrue".equals(hardwareThreadDTO.getTablename())) {
            hardwareThreadDTO.setTablename("hard_ware_thread_now");
        } else {
            hardwareThreadDTO.setTablename("hard_ware_thread");
        }
        models.put("pageView", pageView);
        models.put("hardwareThreadDTO", hardwareThreadDTO);
        ResultVO resultVO = daoClient.getHardWareThread(models);
        logger.info("resultVO=" + resultVO);

        if (!ObjectUtils.isEmpty(resultVO.getCode()) && SoftwareExceptEnum.SUCCESS.getCode().equals(resultVO.getCode())) {
            Object object = resultVO.getData();
            if (!ObjectUtils.isEmpty(object)) {
                String pageViewString = Tools.toJson(object);
                Gson gs = new Gson();
                pageView = gs.fromJson(pageViewString, new TypeToken<PageView>() {
                }.getType());
                logger.info("pageViewString:" + pageViewString);
            } else {
                logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVO.getMsg() + resultVO.getData());
                throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, resultVO.getMsg() + resultVO.getData());
            }
        }
        return pageView;
    }
}
