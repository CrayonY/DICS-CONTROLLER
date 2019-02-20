
package com.ucd.server.service.impl.operationloginfoserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.UUIDUtils;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.operationLogInfoDTO.OperationLogInfoDTO;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.operationloginfoservice.OperationLogInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OperationLogInfoServiceimpl implements OperationLogInfoService {
    @Autowired
    public DaoClient daoClient;
    private final static Logger logger = LoggerFactory.getLogger(OperationLogInfoService.class);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    @Async("transwarpExecutor")
    public String saveOperationLogInfo(OperationLogInfoDTO operationLogInfoDTO) throws Exception {
        if(operationLogInfoDTO == null){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER);
        }
        operationLogInfoDTO.setCreattime(new Date());
        ResultVO resultVO = daoClient.saveOperationLogInfo(operationLogInfoDTO);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO.getData().toString();
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT,  "异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }

    @Override
    public PageView getOperationLogInfo(PageView pageView, OperationLogInfoDTO operationLogInfoDTO) throws Exception {
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("operationLogInfoDTO",operationLogInfoDTO);
        ResultVO resultVO = daoClient.getOperationLogInfo(models);
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
