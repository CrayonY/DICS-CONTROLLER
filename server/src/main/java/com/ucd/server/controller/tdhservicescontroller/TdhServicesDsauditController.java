package com.ucd.server.controller.tdhservicescontroller;


import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhDsauditDTO.TdhDsauditDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsMonthsDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.tdhservicesservice.TdhServicesDsService;
import com.ucd.server.service.tdhservicesservice.TdhServicesDsauditService;
import feign.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/softwareDsaudit")
public class TdhServicesDsauditController {

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(TdhServicesDsauditController.class);

    @Autowired
    private TdhServicesDsauditService tdhServicesDsauditService;





    @PostMapping(value = "/getTdhDsauditInfo")
    public ResultVO getTdhDsauditInfo(PageView pageView, TdhDsauditDTO tdhDsauditDTO){
        ResultVO resultVO = new ResultVO();
        try {
            if(pageView == null){
                pageView = new PageView();
            }
            pageView =tdhServicesDsauditService.getTdhDsauditInfo(pageView,tdhDsauditDTO);
            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),pageView);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    @PostMapping(value = "/saveTdhDsauditData")
    public  ResultVO saveTdhDsauditData(@RequestBody List<TdhDsauditDTO> tdhDsauditDTOList) {
        logger.info("进入controller啦——————————————");
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsauditService.saveTdhDsauditData(tdhDsauditDTOList);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    @PostMapping(value = "/getTdhDsauditData")
    public ResultVO getTdhDsauditData(@RequestBody List<TdhDsauditDTO> tdhDsauditDTOList){
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsauditService.getTdhDsauditData(tdhDsauditDTOList);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }
//
//    /**
//     * 按月，表名，状态对数据同步表做统计展示（配合同步审核处理操作）
//     * @param pageView
//     * @param tdhDsMonthsDTO
//     * @return
//     */
//    @PostMapping(value = "/getTdhDsMonthsInfo")
//    public ResultVO getTdhDsMonthsInfo(PageView pageView, TdhDsMonthsDTO tdhDsMonthsDTO){
//        ResultVO resultVO = new ResultVO();
//        try {
//            if(pageView == null){
//                pageView = new PageView();
//            }
//            pageView =tdhServicesDsService.getTdhDsMonthsInfo(pageView,tdhDsMonthsDTO);
//            resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),pageView);
//            logger.info("resultVO:"+resultVO);
//            return resultVO;
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultVO = ResultVOUtil.error(e);
//            logger.info("resultVO:"+resultVO);
//            return resultVO;
//        }
//    }
//
    /**
     * 审核对端数据同步得请求
     *
     * @param tdhDsauditDTOList
     * @return
     */
    @PostMapping(value = "/auditTdhDsauditListData")
    public ResultVO auditTdhDsauditListData(@RequestBody List<TdhDsauditDTO> tdhDsauditDTOList){
        ResultVO resultVO = new ResultVO();
        try {
            //通过cookie获得用户信息与数据库做校验，不满足直接返回失败，满足进行下一步操作
            resultVO = tdhServicesDsauditService.auditTdhDsauditListData(tdhDsauditDTOList);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    @PostMapping(value = "/countTdhDsauditDataoByAuditStatus")
    public  ResultVO countTdhDsauditDataoByAuditStatus(@Param("auditStatus") Integer auditStatus) {
        logger.info("进入countTdhDsauditDataoByAuditStatuscontroller啦——————————————");
        logger.info("auditStatus——————————————"+auditStatus);
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsauditService.countTdhDsauditDataoByAuditStatus(auditStatus);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }
}
