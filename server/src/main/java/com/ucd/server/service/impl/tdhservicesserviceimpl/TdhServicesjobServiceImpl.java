package com.ucd.server.service.impl.tdhservicesserviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ucd.client.DaoClient;
import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.VO.tdhdsVO.TdhDsVO;
import com.ucd.daocommon.VO.tdhdsVO.tdhdslistVO.TdhDsListVO;
import com.ucd.daocommon.VO.thdServicesVO.TdhServicesJobVO;
import com.ucd.daocommon.VO.thdServicesVO.tdhserviceslistVO.TdhServicesJobListVO;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.tdhservicesservice.TdhServicesjobService;
import com.ucd.server.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TdhServicesjobServiceImpl implements TdhServicesjobService {

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
    @Autowired
    public DaoClient daoClient;

//    @Autowired
//    public LocalDaoClient localDaoClient;


    private final static Logger logger = LoggerFactory.getLogger(TdhServicesjobServiceImpl.class);


    @Override
    public PageView getThdServicesjobInfo(PageView pageView, TdhServicesJobDTO tdhServicesJobDTO) throws Exception {

        if (tdhServicesJobDTO.getCentre() == null || "".equals(tdhServicesJobDTO.getCentre())) {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre中心不能为空");
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "centre中心不能为空");
        }
        if ("A".equals(tdhServicesJobDTO.getCentre())) {
            tdhServicesJobDTO.setCentreTableName("tdha_servicesjob_info");
        } else if ("B".equals(tdhServicesJobDTO.getCentre())) {
            tdhServicesJobDTO.setCentreTableName("tdhb_servicesjob_info");
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_PARAMETER + ",centre参数异常：" + tdhServicesJobDTO.getCentre());
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER, "centre参数异常：" + tdhServicesJobDTO.getCentre());
        }
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("pageView", pageView);
        models.put("tdhServicesJobDTO", tdhServicesJobDTO);
        ResultVO resultVO = daoClient.getThdServicesjobData(models);
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
    public ResultVO getThdServicesjobListData(List<TdhServicesJobDTO> tdhServicesJobDTOList) throws Exception {
        Gson gs = new Gson();
        List<TdhServicesJobVO> tdhServicesJobVOList = new ArrayList<TdhServicesJobVO>();
        if (tdhServicesJobDTOList == null || tdhServicesJobDTOList.size() == 0) {
            throw new SoftwareException(ResultExceptEnum.ERROR_PARAMETER.getCode(), ResultExceptEnum.ERROR_PARAMETER.getMessage());
        }
        ResultVO resultVOTdhServicesJobListVO = daoClient.getThdServicesjobListDataS(tdhServicesJobDTOList);
        if ("000000".equals(resultVOTdhServicesJobListVO.getCode())) {
            logger.info("resultVOTdhServicesJobListVO=" + resultVOTdhServicesJobListVO);
            return resultVOTdhServicesJobListVO;
        } else {
            logger.info("dao层异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultVOTdhServicesJobListVO.getMsg() + resultVOTdhServicesJobListVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, "dao层中心异常:" + resultVOTdhServicesJobListVO.getMsg() + resultVOTdhServicesJobListVO.getData());
        }
    }

    @Override
    @Transactional
    public boolean endToEndSynchronizationData() throws Exception {
        int num = 0;
        //第一步
        List<TdhServicesJobDTO> tdhServicesJobDTOS = new ArrayList<TdhServicesJobDTO>();
        TdhServicesJobDTO tdhaServicesJobDTO = new TdhServicesJobDTO();
        tdhaServicesJobDTO.setCentreTableName("tdha_servicesjob_info");
        tdhServicesJobDTOS.add(tdhaServicesJobDTO);
        TdhServicesJobDTO tdhbServicesJobDTO = new TdhServicesJobDTO();
        tdhbServicesJobDTO.setCentreTableName("tdhb_servicesjob_info");
        tdhServicesJobDTOS.add(tdhbServicesJobDTO);
        //清空“job”表
        daoClient.emptyThdServicesjobListData(tdhServicesJobDTOS);
        //调对端接口，获得“job”表数据，存入本地“job”表数据
        Gson gs = new Gson();
        ResultVO resultJobVO = new ResultVO();
        String result = HttpClientUtils.postString(urlotherside + "/server-0.0.1-SNAPSHOT/softwarejob/getThdServicesjobListData", Tools.toJson(tdhServicesJobDTOS), "application/json", null);
        resultJobVO = gs.fromJson(result, new TypeToken<ResultVO>() {
        }.getType());
        logger.info("resultJobVO:" + resultJobVO);
        if ("000000".equals(resultJobVO.getCode())) {
            List<TdhServicesJobListVO> tdhServicesJobListVOS = new ArrayList<TdhServicesJobListVO>();
            Object object = resultJobVO.getData();
            if (object != null) {
                String tdhServicesJobListVOString = Tools.toJson(object);
                logger.info("tdhServicesJobListVOString:" + tdhServicesJobListVOString);
                tdhServicesJobListVOS = gs.fromJson(tdhServicesJobListVOString, new TypeToken<List<TdhServicesJobListVO>>() {
                }.getType());
                logger.info("tdhServicesJobListVOS:" + tdhServicesJobListVOS);
                if (tdhServicesJobListVOS == null || tdhServicesJobListVOS.size() == 0) {
                    logger.info("tdhServicesJobListVOS为空，不需要同步数据！");
                } else {
                    for (TdhServicesJobListVO tdhServicesJobListVO : tdhServicesJobListVOS) {
                        List<TdhServicesJobVO> tdhServicesJobVOList = tdhServicesJobListVO.getTdhServicesJobVOList();
                        List<TdhServicesJobDTO> tdhServicesJobDTOList = new ArrayList<TdhServicesJobDTO>();
                        if (tdhServicesJobVOList == null || tdhServicesJobVOList.size() == 0) {
                            logger.info("tdhServicesJobVOList为空，不需要同步数据！");
                        } else {
                            for (TdhServicesJobVO tdhServicesJobVO : tdhServicesJobVOList) {
                                TdhServicesJobDTO tdhServicesJobDTO = new TdhServicesJobDTO();
                                BeanUtils.copyProperties(tdhServicesJobVO, tdhServicesJobDTO);
                                tdhServicesJobDTOList.add(tdhServicesJobDTO);
                            }
                            ResultVO resultVO1 = daoClient.saveThdServicesjobListData(tdhServicesJobDTOList);
                            if ("000000".equals(resultVO1.getCode())) {
                                logger.info(tdhServicesJobDTOList.get(0).getCentreTableName() + "表,数据同步完成");
                            } else {
                                logger.info(tdhServicesJobDTOList.get(0).getCentreTableName() + "表,数据同步异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO1.getMsg() + resultVO1.getData());
                                throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, tdhServicesJobDTOList.get(0).getCentreTableName() + "表,数据同步异常：:" + resultVO1.getMsg() + resultVO1.getData());
                            }
                        }
                    }
                }
            }
            num++;
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultJobVO.getMsg() + resultJobVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, resultJobVO.getMsg() + resultJobVO.getData());
        }


        //第二步
        List<TdhDsDTO> tdhDsDTOS = new ArrayList<TdhDsDTO>();
        TdhDsDTO tdhaDsDTO = new TdhDsDTO();
        tdhaDsDTO.setCentreTableName("tdha_ds_info");
        tdhDsDTOS.add(tdhaDsDTO);
        TdhDsDTO tdhbDsDTO = new TdhDsDTO();
        tdhbDsDTO.setCentreTableName("tdhb_ds_info");
        tdhDsDTOS.add(tdhbDsDTO);
        //清空“数据同步”表
        daoClient.emptyThdDsListData(tdhDsDTOS);
        //调对端接口，获得“数据同步”表数据，存入本地“数据同步”表数据
        ResultVO resultDsVO = new ResultVO();
        String resultDs = HttpClientUtils.postString("http://10.66.1.160:28070/softwareDs/getThdDsListData", Tools.toJson(tdhDsDTOS), "application/json", null);
        resultDsVO = gs.fromJson(resultDs, new TypeToken<ResultVO>() {
        }.getType());
        logger.info("resultDsVO:" + resultDsVO);
        if ("000000".equals(resultDsVO.getCode())) {
            List<TdhDsListVO> tdhDsVOS = new ArrayList<TdhDsListVO>();
            Object object = resultDsVO.getData();
            if (object != null) {
                String tdhDsListVOString = Tools.toJson(object);
                logger.info("tdhDsListVOString:" + tdhDsListVOString);
                tdhDsVOS = gs.fromJson(tdhDsListVOString, new TypeToken<List<TdhDsListVO>>() {
                }.getType());
                logger.info("tdhDsVOS:" + tdhDsVOS);
                if (tdhDsVOS == null || tdhDsVOS.size() == 0) {
                    logger.info("tdhDsVOS为空，不需要同步数据！");
                } else {
                    for (TdhDsListVO tdhDsListVO : tdhDsVOS) {
                        List<TdhDsVO> tdhDsVOList = tdhDsListVO.getTdhDsVOList();
                        List<TdhDsDTO> tdhDsDTOList = new ArrayList<TdhDsDTO>();
                        if (tdhDsVOList == null || tdhDsVOList.size() == 0) {
                            logger.info("tdhDsVOList为空，不需要同步数据！");
                        } else {
                            for (TdhDsVO TdhDsVO : tdhDsVOList) {
                                TdhDsDTO tdhDsDTO = new TdhDsDTO();
                                BeanUtils.copyProperties(TdhDsVO, tdhDsDTO);
                                tdhDsDTOList.add(tdhDsDTO);
                            }
                            ResultVO resultVO2 = daoClient.saveTdhDsData(tdhDsDTOList);
                            if ("000000".equals(resultVO2.getCode())) {
                                logger.info(tdhDsDTOList.get(0).getCentreTableName() + "表,数据同步完成");
                            } else {
                                logger.info(tdhDsDTOList.get(0).getCentreTableName() + "表,数据同步异常：e=" + ResultExceptEnum.ERROR_INSERT + "," + resultVO2.getMsg() + resultVO2.getData());
                                throw new SoftwareException(ResultExceptEnum.ERROR_INSERT, tdhDsDTOList.get(0).getCentreTableName() + "表,数据同步异常：:" + resultVO2.getMsg() + resultVO2.getData());
                            }
                        }
                    }
                }
            }
            num++;
        } else {
            logger.info("异常：e=" + ResultExceptEnum.ERROR_SELECT + "," + resultDsVO.getMsg() + resultDsVO.getData());
            throw new SoftwareException(ResultExceptEnum.ERROR_SELECT, resultDsVO.getMsg() + resultDsVO.getData());
        }
        logger.info("-----------------------------------------------num：" + num);
        if (2 == num) {
            return true;
        } else {
            return false;
        }
    }

}
