package com.ucd.server.service.impl.tdhservicesserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultEnum;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.UUIDUtils;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhDsauditDTO.TdhDsauditDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsMonthsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDssyncDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.tdhdsListDTO.TdhDsMonthsListDTO;
import com.ucd.daocommon.VO.tdhdsVO.TdhDsVO;
import com.ucd.daocommon.VO.tdhdsVO.TdhDssyncVO;
import com.ucd.daocommon.VO.tdhdsVO.tdhdslistVO.TdhDsListVO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.service.impl.ServiceSync;
import com.ucd.server.service.impl.ServiceThread;
import com.ucd.server.service.tdhservicesservice.TdhServicesDsService;
import com.ucd.server.utils.HttpClientUtils;
import org.apache.commons.collections.ArrayStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TdhServicesDsServiceImpl implements TdhServicesDsService {

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
    @Value("${basicparameters.urlotherside}")
    public String urlotherside;
    @Value("${basicparameters.centrelocal}")
    public String centrelocal;
    @Autowired
    public DaoClient daoClient;
    @Autowired
    public ServiceSync serviceSync;

//    @Autowired
//    public LocalDaoClient localDaoClient;


    private final static Logger logger = LoggerFactory.getLogger(TdhServicesDsServiceImpl.class);


    @Override
    public String updateDataSynchronizationState(String centre) throws Exception {
        return null;
    }

    @Override
    public PageView getThdServicesDsInfo(PageView pageView, TdhDsDTO tdhDsDTO) throws Exception {
//        if (tdhSDsDTO.getType() == null || "".equals(tdhSDsDTO.getType())){
//            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",type类型不能为空");
//            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"type类型不能为空");
//        }
        if(tdhDsDTO.getCentre() == null || "".equals(tdhDsDTO.getCentre())){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre中心不能为空");
        }
         if ("A".equals(tdhDsDTO.getCentre())) {
             tdhDsDTO.setCentreTableName("tdha_ds_info");
        }else if ("B".equals(tdhDsDTO.getCentre())) {
             tdhDsDTO.setCentreTableName("tdhb_ds_info");
         }else{
             logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsDTO.getCentre());
             throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsDTO.getCentre());
         }
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("tdhDsDTO",tdhDsDTO);
        ResultVO resultVO = daoClient.getTdhDsInfo(models);
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
    public ResultVO getThdDsListData(List<TdhDsDTO> tdhDsDTOS) throws Exception {
        Gson gs = new Gson();
        List<TdhDsVO> tdhDsVOList = new ArrayList<TdhDsVO>();
        if(tdhDsDTOS == null || tdhDsDTOS.size() == 0){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
        }
        ResultVO resultVOTdhDsListVO = daoClient.getThdDsListDataS(tdhDsDTOS);
        if("000000".equals(resultVOTdhDsListVO.getCode())) {
            logger.info("resultVOTdhDsListVO=" + resultVOTdhDsListVO);
            return resultVOTdhDsListVO;
        }else {
            logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOTdhDsListVO.getMsg()+resultVOTdhDsListVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOTdhDsListVO.getMsg()+resultVOTdhDsListVO.getData());
        }
    }

    @Override
    public ResultVO auditThdDsListData(List<TdhDsMonthsDTO> tdhDsMonthsDTOS,String userCode) throws Exception {

        //        2.修改DS的审核状态为“审核中”
        //--1.判断是否是当月要做数据同步T：从月初到当前时间为止，涉及本张表所有的记录，状态更改为“审核中”F：从月初到月末，涉及本张表所有的记录，状态更改为“审核中”
//        ResultVO resultVOTdhDsListVO = daoClient.getThdDsListDataS(tdhDsDTOS);
//        2.向对端发送http审核请求（若  请求不成功，则将审核状态为“审核失败”）
        Gson gs = new Gson();
        if(tdhDsMonthsDTOS == null || tdhDsMonthsDTOS.size() == 0){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
        }
        List<TdhDsauditDTO> tdhDsauditDTOList = new ArrayList<TdhDsauditDTO>();
        for (TdhDsMonthsDTO tdhDsMonthsDTO : tdhDsMonthsDTOS){
            if(tdhDsMonthsDTO.getCentre() == null || "".equals(tdhDsMonthsDTO.getCentre())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre中心不能为空");
            }
            if(tdhDsMonthsDTO.getStartdownTime() == null || "".equals(tdhDsMonthsDTO.getStartdownTime())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",查询时间不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"查询时间不能为空");
            }
            if(tdhDsMonthsDTO.getTableName() == null || "".equals(tdhDsMonthsDTO.getTableName())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",表名不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"表名不能为空");
            }
            if(tdhDsMonthsDTO.getTableNameTotal() == null || "".equals(tdhDsMonthsDTO.getTableNameTotal())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",完整表名不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"完整表名不能为空");
            }
            if ("A".equals(tdhDsMonthsDTO.getCentre())) {
                tdhDsMonthsDTO.setCentreTableName("tdha_ds_info");
            }else if("B".equals(tdhDsMonthsDTO.getCentre())){
                tdhDsMonthsDTO.setCentreTableName("tdhb_ds_info");
            }else{
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsMonthsDTO.getCentre());
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsMonthsDTO.getCentre());
            }
            TdhDsauditDTO tdhDsauditDTO = new TdhDsauditDTO();
            tdhDsauditDTO.setApplyerCode(userCode);
            tdhDsauditDTO.setApplysyncTime(tdhDsMonthsDTO.getStartdownTime());
            tdhDsauditDTO.setApplyTime(new Date());
            tdhDsauditDTO.setTableName(tdhDsMonthsDTO.getTableName());
            tdhDsauditDTO.setAuditStatus(0);
            tdhDsauditDTO.setTableNameall(tdhDsMonthsDTO.getTableNameTotal());
            tdhDsauditDTO.setCentre(centrelocal);
            tdhDsauditDTOList.add(tdhDsauditDTO);
        }


        ResultVO resultVOTdhDsListVO = daoClient.getTdhDsMonthsInfoS(tdhDsMonthsDTOS);
        if("000000".equals(resultVOTdhDsListVO.getCode())) {
            List<TdhDsListVO> tdhDsVOS = new ArrayList<TdhDsListVO>();
            Object object = resultVOTdhDsListVO.getData();
            if (object != null) {
                String tdhDsListVOString = Tools.toJson(object);
                logger.info("tdhDsListVOString:" + tdhDsListVOString);
                tdhDsVOS = gs.fromJson(tdhDsListVOString, new TypeToken<List<TdhDsListVO>>() {
                }.getType());
                logger.info("tdhDsVOS:" + tdhDsVOS);
                if (tdhDsVOS == null || tdhDsVOS.size() == 0){
                    throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
                }else {
                    for (TdhDsListVO tdhDsListVO:tdhDsVOS) {
                        List<TdhDsVO> tdhDsVOList = tdhDsListVO.getTdhDsVOList();
                        if (tdhDsVOList == null || tdhDsVOList.size() == 0){
                            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
                        }else {
                            for (TdhDsVO TdhDsVO : tdhDsVOList) {
                                if (TdhDsVO.getState() == 1){//处理中，说明此表已经审核过了，不需要再次审核
                                    throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER_SYNCSTATE.getCode(),ResultExceptEnum.ERROR_PARAMETER_SYNCSTATE.getMessage());
                                }
                                if (TdhDsVO.getAuditStatus() == 1 ){//1.审核中
                                    throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER_AUDITSTATEING.getCode(),ResultExceptEnum.ERROR_PARAMETER_AUDITSTATEING.getMessage());
                                }
                            }
                        }
                    }
                    //改审核状态为“审核中”
                    Map<String, Object> models = new HashMap<String, Object>();
                    TdhDsMonthsListDTO tdhDsMonthsListDTO = new TdhDsMonthsListDTO();
                    tdhDsMonthsListDTO.setTdhDsMonthsDTOList(tdhDsMonthsDTOS);
                    models.put("auditStatus",1);
                    models.put("userCode",userCode);
                    models.put("tdhDsMonthsListDTO",tdhDsMonthsListDTO);
                    ResultVO resultVO1 = daoClient.updateTdhDsMonthsInfoS(models);
                    if ("000000".equals(resultVO1.getCode())) {
                        logger.info("审核中！修改成功");
                    } else {
                        logger.info("审核中！修改失败");
                        throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE,"审核中！修改失败异常：:" + resultVO1.getMsg() + resultVO1.getData());
                    }

//                    向对端发送http审核请求（若  请求不成功，则将审核状态为“审核失败”）

                    ResultVO resultDsauditVO = new ResultVO();
                    String resultDsaudit = "";
                    try {
                        resultDsaudit = HttpClientUtils.postString(urlotherside+"/server-0.0.1-SNAPSHOT/softwareDsaudit/saveTdhDsauditData", Tools.toJson(tdhDsauditDTOList), "application/json", null);
                        resultDsauditVO = gs.fromJson(resultDsaudit, new TypeToken<ResultVO>() {
                        }.getType());
                        logger.info("resultDsauditVO:"+resultDsauditVO);
                    }catch (Exception e){
                        //审核状态改为“审核失败”
                        logger.info("审核申请通知失败,接口超时等异常");
                        models.put("auditStatus",2);
                        ResultVO resultVO2 = daoClient.updateTdhDsMonthsInfoS(models);
                        if ("000000".equals(resultVO2.getCode())) {
                            logger.info("审核失败！修改成功");
                        } else {
                            logger.info("审核失败！修改失败");
                            throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE,"审核失败！修改失败异常：:" + resultVO2.getMsg() + resultVO2.getData());
                        }
                        throw new SoftwareException(ResultExceptEnum.ERROR_HTTP,"审核失败！修改失败异常：:" + e);
                    }


                    if("000000".equals(resultDsauditVO.getCode())){
                        logger.info("审核申请已通知对端");
                    }else{
                        //审核状态改为“审核失败”
                        logger.info("审核申请通知失败");
                        models.put("auditStatus",2);
                        ResultVO resultVO2 = daoClient.updateTdhDsMonthsInfoS(models);
                        if ("000000".equals(resultVO2.getCode())) {
                            logger.info("审核失败！修改成功");
                        } else {
                            logger.info("审核失败！修改失败");
                            throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE,"审核失败！修改失败异常：:" + resultVO2.getMsg() + resultVO2.getData());
                        }
                    }
                    return resultDsauditVO;
                }
            }else {
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
            }
        }else {
            logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOTdhDsListVO.getMsg()+resultVOTdhDsListVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOTdhDsListVO.getMsg()+resultVOTdhDsListVO.getData());
        }
    }

    @Override
    public PageView getTdhDsMonthsInfo(PageView pageView, TdhDsMonthsDTO tdhDsMonthsDTO) throws Exception {
        if(tdhDsMonthsDTO.getCentre() == null || "".equals(tdhDsMonthsDTO.getCentre())){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre中心不能为空");
        }
        if(tdhDsMonthsDTO.getStartdownTime() == null || "".equals(tdhDsMonthsDTO.getStartdownTime())){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",查询时间不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"查询时间不能为空");
        }

        if ("A".equals(tdhDsMonthsDTO.getCentre())) {
            tdhDsMonthsDTO.setCentreTableName("tdha_ds_info");
        }else if("B".equals(tdhDsMonthsDTO.getCentre())){
            tdhDsMonthsDTO.setCentreTableName("tdhb_ds_info");
        }else{
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsMonthsDTO.getCentre());
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsMonthsDTO.getCentre());
        }
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView",pageView);
        models.put("tdhDsMonthsDTO",tdhDsMonthsDTO);
        ResultVO resultVO = daoClient.getTdhDsMonthsInfo(models);
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
    public ResultVO updateThdDsListData(List<TdhDsMonthsDTO> tdhDsMonthsDTOS) throws Exception {
        Map<String, Object> models1 = new HashMap<String, Object>();
        Map<String, Object> models2 = new HashMap<String, Object>();
        TdhDsMonthsListDTO tdhDsMonthsListDTO1 = new TdhDsMonthsListDTO();
        TdhDsMonthsListDTO tdhDsMonthsListDTO2 = new TdhDsMonthsListDTO();
        List<TdhDsMonthsDTO> tdhDsMonthsDTOList1 = new ArrayList<TdhDsMonthsDTO>();
        List<TdhDsMonthsDTO> tdhDsMonthsDTOList2 = new ArrayList<TdhDsMonthsDTO>();
        int count1 = 0;
        int count2 = 0;
        for (TdhDsMonthsDTO tdhDsMonthsDTO : tdhDsMonthsDTOS){
            if(tdhDsMonthsDTO.getCentre() == null || "".equals(tdhDsMonthsDTO.getCentre())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre中心不能为空");
            }
            if(tdhDsMonthsDTO.getStartdownTime() == null || "".equals(tdhDsMonthsDTO.getStartdownTime())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",查询时间不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"查询时间不能为空");
            }
            if(tdhDsMonthsDTO.getTableName() == null || "".equals(tdhDsMonthsDTO.getTableName())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",表名不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"表名不能为空");
            }
            if(tdhDsMonthsDTO.getTableNameTotal() == null || "".equals(tdhDsMonthsDTO.getTableNameTotal())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",完整表名不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"完整表名不能为空");
            }
            if(tdhDsMonthsDTO.getAuditStatus() == null ){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",审核状态不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"审核状态不能为空");
            }
            if ("A".equals(tdhDsMonthsDTO.getCentre())) {
                tdhDsMonthsDTO.setCentreTableName("tdha_ds_info");
            }else if("B".equals(tdhDsMonthsDTO.getCentre())){
                tdhDsMonthsDTO.setCentreTableName("tdhb_ds_info");
            }else{
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsMonthsDTO.getCentre());
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsMonthsDTO.getCentre());
            }
            if (tdhDsMonthsDTO.getAuditStatus() == 3){//通过
                models1.put("auditStatus",tdhDsMonthsDTO.getAuditStatus());
                models1.put("operCode",tdhDsMonthsDTO.getUserCode());
                tdhDsMonthsDTOList1.add(tdhDsMonthsDTO);
                count1 ++;
            }else if (tdhDsMonthsDTO.getAuditStatus() == 4){//拒绝
                models2.put("auditStatus",tdhDsMonthsDTO.getAuditStatus());
                models2.put("operCode",tdhDsMonthsDTO.getUserCode());
                tdhDsMonthsDTOList2.add(tdhDsMonthsDTO);
                count2 ++;
            }
        }
        logger.info("===================count1:"+count1+"--------------count2"+count2);
        tdhDsMonthsListDTO1.setTdhDsMonthsDTOList(tdhDsMonthsDTOList1);
        tdhDsMonthsListDTO2.setTdhDsMonthsDTOList(tdhDsMonthsDTOList2);
        models1.put("tdhDsMonthsListDTO",tdhDsMonthsListDTO1);
        models2.put("tdhDsMonthsListDTO",tdhDsMonthsListDTO2);
        ResultVO resultVO = new ResultVO();
        int successNum = 0;
        if (count1 != 0) {
            ResultVO resultVO1 = daoClient.updateTdhDsMonthsInfoS(models1);
            if ("000000".equals(resultVO1.getCode())) {
                logger.info("审核通过！修改成功");
                successNum ++;
            } else {
                logger.info("审核通过！修改失败");
                throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "审核通过！修改失败异常：:" + resultVO1.getMsg() + resultVO1.getData());
            }
        }else{
            logger.info("没有审核通过的请求");
        }
        if (count2 != 0) {
            ResultVO resultVO2 = daoClient.updateTdhDsMonthsInfoS(models2);
            if ("000000".equals(resultVO2.getCode())) {
                logger.info("审核拒绝！修改成功");
                successNum ++;
            } else {
                logger.info("审核拒绝！修改失败");
                throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "审核中！修改失败异常：:" + resultVO2.getMsg() + resultVO2.getData());
            }
        }else {
            logger.info("没有审核拒绝的请求");
        }
        if (successNum != 0){
            resultVO.setData(count1+count2);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(), TdhServicesReturnEnum.SUCCESS.getMessage(),count1+count2);
        }
        return resultVO;
    }

    @Override
    public ResultVO syncThdDsListData(List<TdhDsMonthsDTO> tdhDsMonthsDTOS,String userCode) throws Exception {
        ResultVO resultVO = new ResultVO();
        Gson gs = new Gson();
        if(tdhDsMonthsDTOS == null || tdhDsMonthsDTOS.size() == 0){
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
        }
        List<TdhDssyncDTO> tdhDssyncDTOList = new ArrayList<TdhDssyncDTO>();
        for (TdhDsMonthsDTO tdhDsMonthsDTO : tdhDsMonthsDTOS){
            if(tdhDsMonthsDTO.getCentre() == null || "".equals(tdhDsMonthsDTO.getCentre())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre中心不能为空");
            }
            if(tdhDsMonthsDTO.getStartdownTime() == null || "".equals(tdhDsMonthsDTO.getStartdownTime())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",查询时间不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"查询时间不能为空");
            }
            if(tdhDsMonthsDTO.getTableName() == null || "".equals(tdhDsMonthsDTO.getTableName())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",表名不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"表名不能为空");
            }
            if(tdhDsMonthsDTO.getTableNameTotal() == null || "".equals(tdhDsMonthsDTO.getTableNameTotal())){
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",完整表名不能为空");
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"完整表名不能为空");
            }
            if ("A".equals(tdhDsMonthsDTO.getCentre())) {
                tdhDsMonthsDTO.setCentreTableName("tdha_ds_info");
            }else if("B".equals(tdhDsMonthsDTO.getCentre())){
                tdhDsMonthsDTO.setCentreTableName("tdhb_ds_info");
            }else{
                logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsMonthsDTO.getCentre());
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsMonthsDTO.getCentre());
            }
            TdhDssyncDTO tdhDssyncDTO = new TdhDssyncDTO();
            String ID = KeyUtil.genUniqueKey();
            tdhDssyncDTO.setId(ID + UUIDUtils.getUUID());
            tdhDssyncDTO.setUserCode(userCode);
            tdhDssyncDTO.setSyncMonth(tdhDsMonthsDTO.getStartdownTime());
            tdhDssyncDTO.setSyncTime(new Date());
            tdhDssyncDTO.setTableName(tdhDsMonthsDTO.getTableName());
            tdhDssyncDTO.setState(1);
            tdhDssyncDTO.setTableNameall(tdhDsMonthsDTO.getTableNameTotal());
            tdhDssyncDTOList.add(tdhDssyncDTO);
        }
        ResultVO resultVOTdhDsListVO = daoClient.getTdhDsMonthsInfoS(tdhDsMonthsDTOS);
        if("000000".equals(resultVOTdhDsListVO.getCode())) {
            List<TdhDsListVO> tdhDsVOS = new ArrayList<TdhDsListVO>();
            Object object = resultVOTdhDsListVO.getData();
            if (object != null) {
                String tdhDsListVOString = Tools.toJson(object);
                logger.info("tdhDsListVOString:" + tdhDsListVOString);
                tdhDsVOS = gs.fromJson(tdhDsListVOString, new TypeToken<List<TdhDsListVO>>() {
                }.getType());
                logger.info("tdhDsVOS:" + tdhDsVOS);
                if (tdhDsVOS == null || tdhDsVOS.size() == 0){
                    throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
                }else {
                    for (TdhDsListVO tdhDsListVO:tdhDsVOS) {
                        List<TdhDsVO> tdhDsVOList = tdhDsListVO.getTdhDsVOList();
                        if (tdhDsVOList == null || tdhDsVOList.size() == 0){
                            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
                        }else {
                            for (TdhDsVO TdhDsVO : tdhDsVOList) {
                                if (TdhDsVO.getState() == 1 ){//同步中，说明此表正在处理，不可再次同步
                                    throw new SoftwareException(ResultExceptEnum.RROR_PARAMETER_SYNCSTATEING.getCode(),ResultExceptEnum.RROR_PARAMETER_SYNCSTATEING.getMessage());
                                }
                                if (TdhDsVO.getAuditStatus() != 3 ){//1参数有误，审核未通过，不可同步操作
                                    throw new SoftwareException(ResultExceptEnum.RROR_PARAMETER_AUDITSTATEFAIL.getCode(),ResultExceptEnum.RROR_PARAMETER_AUDITSTATEFAIL.getMessage());
                                }
                            }
                        }
                    }
//调取数据同步方法
                    String testFlag = "";
                    try {
                        testFlag = serviceSync.SyncThdListDataThread(tdhDssyncDTOList.get(0));
                    }catch (Exception e){
                        logger.info("数据同步shell脚本失败");
                        throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "数据同步shell脚本失败!异常：:" + testFlag);
                    }
                    //通知数据同步shell（异步）
                    if("OK".equals(testFlag)) {
                        Map<String, Object> models = new HashMap<String, Object>();
                        TdhDsMonthsListDTO tdhDsMonthsListDTO = new TdhDsMonthsListDTO();
                        tdhDsMonthsListDTO.setTdhDsMonthsDTOList(tdhDsMonthsDTOS);
                        models.put("syncState", 1);
                        models.put("operCode", userCode);
                        models.put("tdhDsMonthsListDTO", tdhDsMonthsListDTO);
                        ResultVO resultVO1 = daoClient.updateTdhDsMonthsInfoS(models);
                        if ("000000".equals(resultVO1.getCode())) {
                            logger.info("DS表同步状态“同步中”！修改成功");
                        } else {
                            logger.info("DS表同步状态“同步中”！修改失败");
                            throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "DS表同步状态“同步中”！修改失败异常：:" + resultVO1.getMsg() + resultVO1.getData());
                        }
                        ResultVO resultVO2 = daoClient.saveTdhDssyncData(tdhDssyncDTOList);
                        if ("000000".equals(resultVO2.getCode())) {
                            logger.info("添加DSSYNC表！添加成功");
                        } else {
                            logger.info("添加DSSYNC表！添加失败");
                            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "添加DSSYNC表！添加失败异常：:" + resultVO2.getMsg() + resultVO2.getData());
                        }
                        resultVO = ResultVOUtil.setResult(ResultEnum.RESULT_SUCCESS.getCode(),ResultEnum.RESULT_SUCCESS.getMessage(),testFlag);
                        return resultVO;
                    }else {
                        logger.info("通知数据同步shell失败");
                        throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "通知数据同步shell失败!异常：:" + testFlag);
                    }
                }
            }else {
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
            }
        }else {
            logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOTdhDsListVO.getMsg()+resultVOTdhDsListVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOTdhDsListVO.getMsg()+resultVOTdhDsListVO.getData());
        }
    }

    @Override
    public ResultVO syncResult(String result, String id) throws Exception {
        ResultVO resultVO = new ResultVO();
        Gson gs = new Gson();
        if (result == null || "".equals(result)){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",result不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"result不能为空");
        }
        if (id == null || "".equals(id)){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",id不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"id不能为空");
        }
        TdhDssyncDTO tdhDssyncDTO = new TdhDssyncDTO();
        tdhDssyncDTO.setId(id);
        if("Y".equals(result)){
            tdhDssyncDTO.setState(2);
        }else {
            tdhDssyncDTO.setState(3);
        }
        ResultVO resultVOtdhDssyncVO = daoClient.getTdhDssyncInfoById(tdhDssyncDTO);
        if("000000".equals(resultVOtdhDssyncVO.getCode())) {
            TdhDssyncVO tdhDssyncVO = new TdhDssyncVO();
            Object object = resultVOtdhDssyncVO.getData();
            if (object != null) {
                String tdhDssyncVOString = Tools.toJson(object);
                logger.info("tdhDssyncVOString:" + tdhDssyncVOString);
                tdhDssyncVO = gs.fromJson(tdhDssyncVOString, new TypeToken<TdhDssyncVO>() {
                }.getType());
                logger.info("tdhDssyncVO:" + tdhDssyncVO);
                if (tdhDssyncVO == null) {
                    throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(), ResultExceptEnum.ERROR_PARAMETER.getMessage());
                } else {
                    ResultVO resultVO1 = daoClient.updateTdhDssyncData(tdhDssyncDTO);
                    if ("000000".equals(resultVO1.getCode())) {
                        logger.info("DSsync表同步状态修改成功");
                    } else {
                        logger.info("DSsync表同步状态修改失败");
                        throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "DS表同步状态修改失败异常：:" + resultVO1.getMsg() + resultVO1.getData());
                    }
                    List<TdhDsMonthsDTO> tdhDsMonthsDTOS = new ArrayList<TdhDsMonthsDTO>();
                    TdhDsMonthsDTO tdhDsMonthsDTO = new TdhDsMonthsDTO();
                    tdhDsMonthsDTO.setCentre(centrelocal);
                    tdhDsMonthsDTO.setTableName(tdhDssyncVO.getTableName());
                    tdhDsMonthsDTO.setTableNameTotal(tdhDssyncVO.getTableNameall());
                    tdhDsMonthsDTO.setStartdownTime(tdhDssyncVO.getSyncMonth());
                    if ("A".equals(tdhDsMonthsDTO.getCentre())) {
                        tdhDsMonthsDTO.setCentreTableName("tdha_ds_info");
                    }else if("B".equals(tdhDsMonthsDTO.getCentre())){
                        tdhDsMonthsDTO.setCentreTableName("tdhb_ds_info");
                    }else{
                        logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsMonthsDTO.getCentre());
                        throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsMonthsDTO.getCentre());
                    }
                    tdhDsMonthsDTOS.add(tdhDsMonthsDTO);
                    Map<String, Object> models = new HashMap<String, Object>();
                    TdhDsMonthsListDTO tdhDsMonthsListDTO = new TdhDsMonthsListDTO();
                    tdhDsMonthsListDTO.setTdhDsMonthsDTOList(tdhDsMonthsDTOS);
                    models.put("syncState", tdhDssyncDTO.getState());
                    models.put("operCode", "gwm");
                    models.put("tdhDsMonthsListDTO", tdhDsMonthsListDTO);
                    ResultVO resultVO2 = daoClient.updateTdhDsMonthsInfoS(models);
                    if ("000000".equals(resultVO1.getCode())) {
                        logger.info("DS表同步状态修改成功");
                    } else {
                        logger.info("DS表同步状态修改失败");
                        throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "DS表同步状态修改失败异常：:" + resultVO2.getMsg() + resultVO2.getData());
                    }
                    ResultVO resultVOtdhDssyncVO1 = daoClient.getTdhDssyncInfoByState(1);
                    if ("000000".equals(resultVO1.getCode())) {
                        logger.info("DSsync表信息获取成功");
                        List<TdhDssyncVO> tdhDssyncVOS = new ArrayList<TdhDssyncVO>();
                        Object object1 = resultVOtdhDssyncVO1.getData();
                        if (object1 != null) {
                            String tdhDssyncVO1String = Tools.toJson(object1);
                            logger.info("tdhDssyncVO1String:" + tdhDssyncVO1String);
                            tdhDssyncVOS = gs.fromJson(tdhDssyncVO1String, new TypeToken<List<TdhDssyncVO>>() {
                            }.getType());
                            logger.info("tdhDssyncVO:" + tdhDssyncVO);
                            if (tdhDssyncVOS == null || tdhDssyncVOS.size() == 0) {
                                logger.info("目前没有审核通过且没有同步的数据");
                                resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),"目前没有审核通过且没有同步的数据");
                                return resultVO;
                            } else {
                                logger.info("进行下一条数据同步表单数据");
                                //调取数据同步方法
                                TdhDssyncDTO tdhDssyncDTO1 = new TdhDssyncDTO();
                                tdhDssyncDTO1.setId(tdhDssyncVOS.get(0).getId());
                                tdhDssyncDTO1.setUserCode(tdhDssyncVOS.get(0).getUserCode());
                                tdhDssyncDTO1.setSyncMonth(tdhDssyncVOS.get(0).getSyncMonth());
                                tdhDssyncDTO1.setSyncTime(tdhDssyncVOS.get(0).getSyncTime());
                                tdhDssyncDTO1.setTableName(tdhDssyncVOS.get(0).getTableName());
                                tdhDssyncDTO1.setState(tdhDssyncVOS.get(0).getState());
                                tdhDssyncDTO1.setTableNameall(tdhDssyncVOS.get(0).getTableNameall());
                                String testFlag = "";
                                try {
                                    testFlag = serviceSync.SyncThdListDataThread(tdhDssyncDTO1);
                                } catch (Exception e) {
                                    logger.info("数据同步shell脚本失败");
                                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "数据同步shell脚本失败!异常：:" + testFlag);
                                }
                                //通知数据同步shell（异步）
                                if ("OK".equals(testFlag)) {
                                    logger.info("通知数据同步shell成功");
                                    resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),"通知数据同步shell成功");
                                    return resultVO;
                                    } else {
                                    logger.info("通知数据同步shell失败");
                                    throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "通知数据同步shell失败!异常：:" + testFlag);
                                }
                            }
                        } else {
                            logger.info("DSsync表信息获取失败");
                            throw new SoftwareException(ResultExceptEnum.ERROR_UPDATE, "DSsync表信息获取失败异常：:" + resultVOtdhDssyncVO1.getMsg() + resultVOtdhDssyncVO1.getData());
                        }
                    }else {
                        logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOtdhDssyncVO1.getMsg()+resultVOtdhDssyncVO1.getData());
                        throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOtdhDssyncVO1.getMsg()+resultVOtdhDssyncVO1.getData());
                    }
                }
            }else {
                logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
                throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(),ResultExceptEnum.ERROR_PARAMETER.getMessage());
            }
        }else {
            logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOtdhDssyncVO.getMsg()+resultVOtdhDssyncVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOtdhDssyncVO.getMsg()+resultVOtdhDssyncVO.getData());
        }
    }

    @Override
    public ResultVO countTdhDsauditDataoByAuditStatus(TdhDsDTO tdhDsDTO) throws Exception {
        if(tdhDsDTO.getCentre() == null || "".equals(tdhDsDTO.getCentre())){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre中心不能为空");
        }
        if(tdhDsDTO.getState() == null ){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",state不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"state不能为空");
        }
        if(tdhDsDTO.getAuditStatus() == null ){
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",AuditStatus不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"AuditStatus不能为空");
        }
        if ("A".equals(tdhDsDTO.getCentre())) {
            tdhDsDTO.setCentreTableName("tdha_ds_info");
        }else if ("B".equals(tdhDsDTO.getCentre())) {
            tdhDsDTO.setCentreTableName("tdhb_ds_info");
        }else{
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常："+tdhDsDTO.getCentre());
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER,"centre参数异常："+tdhDsDTO.getCentre());
        }
        ResultVO resultVO = daoClient.countTdhDsDataByAuditStatusAndState(tdhDsDTO);
        logger.info("resultVO=" + resultVO);
        if ("000000".equals(resultVO.getCode())) {
            return resultVO;
        } else {
            logger.info( "中心异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO.getMsg()+resultVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, "中心异常:" + resultVO.getMsg()+resultVO.getData());
        }
    }
}
