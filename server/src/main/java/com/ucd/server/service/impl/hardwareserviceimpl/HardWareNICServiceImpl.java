package com.ucd.server.service.impl.hardwareserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareCpuDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareNicDTO;
import com.ucd.server.enums.SoftwareExceptEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.hardwareservice.HardWareCPUService;
import com.ucd.server.service.hardwareservice.HardWareNICService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crayon on 2019/4/1.
 */
@Service
public class HardWareNICServiceImpl implements HardWareNICService {

    @Autowired
    public DaoClient daoClient;

    private final static Logger logger = LoggerFactory.getLogger(HardWareNICService.class);


    @Override
    public PageView getHardWareNIC(PageView pageView, HardwareNicDTO hardwareNicDTO) throws Exception {
        Map<String, Object> models = new HashMap<String, Object>();
        logger.info("hardwareNicDTO:" + hardwareNicDTO);
        if (hardwareNicDTO.getTableName() == null || "".equals(hardwareNicDTO.getTableName())){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",表名不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"表名不能为空");
        }
        if("nowtrue".equals(hardwareNicDTO.getTableName())){
            hardwareNicDTO.setTableName("hard_ware_nic_now");
        }else {
            hardwareNicDTO.setTableName("hard_ware_nic");
        }
        models.put("pageView", pageView);
        models.put("hardwareNicDTO", hardwareNicDTO);
        ResultVO resultVO = daoClient.getHardWareNic(models);
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