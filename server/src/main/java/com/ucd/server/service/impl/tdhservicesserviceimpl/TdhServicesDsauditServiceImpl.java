package com.ucd.server.service.impl.tdhservicesserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhDsauditDTO.TdhDsauditDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsMonthsDTO;
import com.ucd.daocommon.VO.tdhDsauditVO.TdhDsauditVO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.tdhservicesservice.TdhServicesDsauditService;
import com.ucd.server.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TdhServicesDsauditServiceImpl implements TdhServicesDsauditService {

    @Value("${basicparameters.centrelocal}")
    public String centrelocal;
    @Value("${basicparameters.urlotherside}")
    public String urlotherside;
    @Autowired
    public DaoClient daoClient;

//    @Autowired
//    public LocalDaoClient localDaoClient;


    private final static Logger logger = LoggerFactory.getLogger(TdhServicesDsauditServiceImpl.class);


    @Override
    public ResultVO saveTdhDsauditInfo(TdhDsauditDTO tdhDsauditDTO) throws Exception {
        if(tdhDsauditDTO == null ){
            throw new SoftwareException(TdhServicesReturnEnum.PARAM_ERROR.getCode(),TdhServicesReturnEnum.PARAM_ERROR.getMessage());
        }
        ResultVO resultVO = daoClient.saveTdhDsauditInfo(tdhDsauditDTO);
        return resultVO;
    }

    @Override
    public ResultVO saveTdhDsauditData(List<TdhDsauditDTO> tdhDsauditDTOList) throws Exception {
        if(tdhDsauditDTOList == null || tdhDsauditDTOList.size() == 0){
            throw new SoftwareException(TdhServicesReturnEnum.PARAM_ERROR.getCode(),TdhServicesReturnEnum.PARAM_ERROR.getMessage());
        }
        ResultVO resultVO = daoClient.saveTdhDsauditData(tdhDsauditDTOList);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO;
        } else {
            logger.info( "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "中心异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }

    @Override
    public PageView getTdhDsauditInfo(PageView pageView, TdhDsauditDTO tdhDsauditDTO) throws Exception {
        if(tdhDsauditDTO == null ){
            throw new SoftwareException(TdhServicesReturnEnum.PARAM_ERROR.getCode(),TdhServicesReturnEnum.PARAM_ERROR.getMessage());
        }
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("tdhDsauditDTO",tdhDsauditDTO);
        ResultVO resultVO = daoClient.getTdhDsauditInfo(models);
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
    public ResultVO getTdhDsauditData(List<TdhDsauditDTO> tdhDsauditDTOList) throws Exception {
        Gson gs = new Gson();
        if(tdhDsauditDTOList == null || tdhDsauditDTOList.size() == 0){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
        }
        for (TdhDsauditDTO tdhDsauditDTO : tdhDsauditDTOList){
            if(tdhDsauditDTO.getId() == null || "".equals(tdhDsauditDTO.getId())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",ID不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"ID不能为空");
            }
        }
        return null;
    }

    @Override
    public ResultVO auditTdhDsauditListData(List<TdhDsauditDTO> tdhDsauditDTOList,String userCode) throws Exception {
        String centre = "";
        if (centrelocal.equals("A")){
            centre = "B";
        }else if (centrelocal.equals("B")){
            centre = "A";
        }
        Gson gs = new Gson();
        if(tdhDsauditDTOList == null || tdhDsauditDTOList.size() == 0){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
        }
        List<TdhDsMonthsDTO> tdhDsMonthsDTOS = new ArrayList<TdhDsMonthsDTO>();
        for (TdhDsauditDTO tdhDsauditDTO:tdhDsauditDTOList){
            if(tdhDsauditDTO == null || tdhDsauditDTO.getId() == null || "".equals(tdhDsauditDTO.getId()) || tdhDsauditDTO.getAuditStatus() == null){
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
            }
            boolean flag = true;
            if (tdhDsauditDTO.getAuditStatus() == 3 || tdhDsauditDTO.getAuditStatus() == 4){
                flag = false;
            }
            if (flag){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",审核状态输入错误");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"审核状态输入错误");
            }
            tdhDsauditDTO.setAuditerCode(userCode);
            TdhDsMonthsDTO tdhDsMonthsDTO = new TdhDsMonthsDTO();
            tdhDsMonthsDTO.setAuditStatus(tdhDsauditDTO.getAuditStatus());
            tdhDsMonthsDTO.setUserCode(tdhDsauditDTO.getAuditerCode());
            tdhDsMonthsDTO.setTableName(tdhDsauditDTO.getTableName());
            tdhDsMonthsDTO.setTableNameTotal(tdhDsauditDTO.getTableNameall());
            tdhDsMonthsDTO.setStartdownTime(tdhDsauditDTO.getApplysyncTime()); 
            tdhDsMonthsDTO.setCentre(centre);
            tdhDsMonthsDTOS.add(tdhDsMonthsDTO);
        }
        ResultVO resultVOTdhDsauditListVO = daoClient.getTdhDsauditListDataS(tdhDsauditDTOList);
        if("000000".equals(resultVOTdhDsauditListVO.getCode())) {
            List<TdhDsauditVO> tdhDsauditVOS = new ArrayList<TdhDsauditVO>();
            Object object = resultVOTdhDsauditListVO.getData();
            if (object != null) {
                String tdhDsauditListVOString = Tools.toJson(object);
                logger.info("tdhDsauditListVOString:" + tdhDsauditListVOString);
                tdhDsauditVOS = gs.fromJson(tdhDsauditListVOString, new TypeToken<List<TdhDsauditVO>>() {
                }.getType());
                logger.info("tdhDsauditVOS:" + tdhDsauditVOS);
                if (tdhDsauditVOS == null || tdhDsauditVOS.size() == 0){
                    throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
                }else {
                    for (TdhDsauditVO tdhDsauditVO:tdhDsauditVOS) {
                        if (tdhDsauditVO.getAuditStatus() == 3 || tdhDsauditVO.getAuditStatus() == 4){//已经处理，说明此表已经审核过了，不需要再次审核
                            throw new SoftwareException(ResultExceptEnum.RROR_PARAMETER_AUDITSTATED.getCode(),ResultExceptEnum.RROR_PARAMETER_AUDITSTATED.getMessage());
                        }
                    }
//                    向对端发送http审核请求
                    ResultVO resultDsauditVO = new ResultVO();
                    String resultDsaudit = "";
                    try {
                        resultDsaudit = HttpClientUtils.postString(urlotherside+"/server-0.0.1-SNAPSHOT/softwareDs/updateThdDsListData", Tools.toJson(tdhDsMonthsDTOS), "application/json", null);
                        resultDsauditVO = gs.fromJson(resultDsaudit, new TypeToken<ResultVO>() {
                        }.getType());
                    }catch (Exception e){
                        logger.info("审核结果通知失败,接口超时等异常");
                        throw new SoftwareException(ResultExceptEnum.ERROR_HTTP,"审核结果通知失败,接口超时等异常：:" + e);
                    }
                    logger.info("resultDsauditVO:"+resultDsauditVO);
                    if("000000".equals(resultDsauditVO.getCode())){
                        logger.info("审核结果已通知对端");
                        //修改审核表对应状态
                        ResultVO resultVO1 = daoClient.updateTdhDsauditDataS(tdhDsauditDTOList);
                        if ("000000".equals(resultVO1.getCode())) {
                            logger.info("审核状态修改成功");
                        } else {
                            logger.info("审核状态修改失败");
                            throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE,"审核状态修改失败异常：:" + resultVO1.getMsg() + resultVO1.getData());
                        }
                    }else{
                        logger.info("审核结果通知失败");
                        throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE,"审核结果通知失败异常：:" + resultDsauditVO.getMsg() + resultDsauditVO.getData());
                    }
                    return resultDsauditVO;
                }
            }else {
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
            }
        }else {
            logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOTdhDsauditListVO.getMsg()+resultVOTdhDsauditListVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOTdhDsauditListVO.getMsg()+resultVOTdhDsauditListVO.getData());
        }
    }

    @Override
    public ResultVO countTdhDsauditDataoByAuditStatus(Integer auditStatus) throws Exception {
        if(auditStatus == null){
            throw new SoftwareException(TdhServicesReturnEnum.PARAM_ERROR.getCode(),TdhServicesReturnEnum.PARAM_ERROR.getMessage());
        }
        ResultVO resultVO = daoClient.countTdhDsauditDataoByAuditStatus(auditStatus);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO;
        } else {
            logger.info( "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "中心异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }
}
