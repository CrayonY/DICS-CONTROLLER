package com.ucd.server.controller.tdhservicescontroller;


import com.ucd.common.VO.ResultVO;
import com.ucd.common.enums.ResultEnum;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.operationLogInfoDTO.OperationLogInfoDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsMonthsDTO;
import com.ucd.server.enums.OperationLogInfoEnum;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.operationloginfoservice.OperationLogInfoService;
import com.ucd.server.service.tdhservicesservice.TdhServicesDsService;
import feign.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/softwareDs")
public class TdhServicesDsController {

    /**
     * 引入日志，注意都是"org.slf4j"包下
     */
    private final static Logger logger = LoggerFactory.getLogger(TdhServicesDsController.class);

    @Autowired
    private TdhServicesDsService tdhServicesDsService;

    @Autowired
    private OperationLogInfoService operationLogInfoService;





    @PostMapping(value = "/getThdServicesDsInfo")
    public ResultVO getThdServicesDsInfo(PageView pageView, TdhDsDTO tdhSDsDTO){
        ResultVO resultVO = new ResultVO();
        try {
            if(pageView == null){
                pageView = new PageView();
            }
            pageView =tdhServicesDsService.getThdServicesDsInfo(pageView,tdhSDsDTO);
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

    @PostMapping(value = "/getThdDsListData")
    public ResultVO getThdDsListData(@RequestBody List<TdhDsDTO> tdhDsDTOS){
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsService.getThdDsListData(tdhDsDTOS);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    /**
     * 按月，表名，状态对数据同步表做统计展示（配合同步审核处理操作）
     * @param pageView
     * @param tdhDsMonthsDTO
     * @return
     */
    @PostMapping(value = "/getTdhDsMonthsInfo")
    public ResultVO getTdhDsMonthsInfo(PageView pageView, TdhDsMonthsDTO tdhDsMonthsDTO){
        ResultVO resultVO = new ResultVO();
        try {
            if(pageView == null){
                pageView = new PageView();
            }
            pageView =tdhServicesDsService.getTdhDsMonthsInfo(pageView,tdhDsMonthsDTO);
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

    /**
     * 向对端发送要进行数据同步的审核
     * @param tdhDsMonthsDTOS
     * @return
     */
    @PostMapping(value = "/auditThdDsListData")
    public ResultVO auditThdDsListData(@RequestBody List<TdhDsMonthsDTO> tdhDsMonthsDTOS, HttpServletRequest req){
        ResultVO resultVO = new ResultVO();
        String userCode = "";
        try {
            //通过cookie获得用户信息与数据库做校验，不满足直接返回失败，满足进行下一步操作
            req.setCharacterEncoding("utf-8");
            //获取cookie
            Cookie cookies[] = req.getCookies();
            if(cookies==null || cookies.length == 0){
                logger.info("没有cookie");
                throw new SoftwareException(ResultExceptEnum.ERROR_HTTP_COOKIE.getCode(),ResultExceptEnum.ERROR_HTTP_COOKIE.getMessage());
            }else{
                for (Cookie cookie : cookies){

                    //获取cookie的解释内容
                    String comment = cookie.getComment();
                    System.out.println("comment:"+comment);
                    //获取cookie的键
                    String key = cookie.getName();
                    System.out.println("key:"+key);
                    if ("userCode".equals(key)) {

                        //获取cookie的值
                        String value = cookie.getValue();
                        System.out.println("value:" + value);
                        userCode = value;
                    }

                    //获取cookie的有效时间。
                    int time = cookie.getMaxAge();
                    System.out.println("time:"+time);

                    //获取服务器的IP对应的域名
                    String domain = cookie.getDomain();
                    System.out.println("domain:"+ domain);

                    //获取有效路径
                    String path = cookie.getPath();
                    System.out.println("path:"+ path);

                }
            }
            OperationLogInfoDTO operationLogInfoDTO = new OperationLogInfoDTO();
            operationLogInfoDTO.setUserCode(userCode);
            operationLogInfoDTO.setValue(userCode+OperationLogInfoEnum.auditThdDsListData.getMessage());
            operationLogInfoService.saveOperationLogInfo(operationLogInfoDTO);
//            resultVO = tdhServicesDsService.auditThdDsListData(tdhDsMonthsDTOS,userCode);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    /**
     * 向对端发送要进行数据同步的审核结果
     * @param tdhDsMonthsDTOS
     * @return
     */
    @PostMapping(value = "/updateThdDsListData")
    public ResultVO updateThdDsListData(@RequestBody List<TdhDsMonthsDTO> tdhDsMonthsDTOS){
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsService.updateThdDsListData(tdhDsMonthsDTOS);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    /**
     * 数据同步
     * @param tdhDsMonthsDTOS
     * @return
             */
    @PostMapping(value = "/syncThdDsListData")
    public ResultVO syncThdDsListData(@RequestBody List<TdhDsMonthsDTO> tdhDsMonthsDTOS, HttpServletRequest req){
        ResultVO resultVO = new ResultVO();
        String userCode = "";
        try {
            //通过cookie获得用户信息与数据库做校验，不满足直接返回失败，满足进行下一步操作
            req.setCharacterEncoding("utf-8");
            //获取cookie
            Cookie cookies[] = req.getCookies();
            if(cookies==null || cookies.length == 0){
                logger.info("没有cookie");
                throw new SoftwareException(ResultExceptEnum.ERROR_HTTP_COOKIE.getCode(),ResultExceptEnum.ERROR_HTTP_COOKIE.getMessage());
            }else{
                for (Cookie cookie : cookies){

                    //获取cookie的解释内容
                    String comment = cookie.getComment();
                    System.out.println("comment:"+comment);
                    //获取cookie的键
                    String key = cookie.getName();
                    System.out.println("key:"+key);
                    if ("userCode".equals(key)) {

                        //获取cookie的值
                        String value = cookie.getValue();
                        System.out.println("value:" + value);
                        userCode = value;
                    }

                    //获取cookie的有效时间。
                    int time = cookie.getMaxAge();
                    System.out.println("time:"+time);

                    //获取服务器的IP对应的域名
                    String domain = cookie.getDomain();
                    System.out.println("domain:"+ domain);

                    //获取有效路径
                    String path = cookie.getPath();
                    System.out.println("path:"+ path);

                }
            }
            OperationLogInfoDTO operationLogInfoDTO = new OperationLogInfoDTO();
            operationLogInfoDTO.setUserCode(userCode);
            operationLogInfoDTO.setValue(userCode+OperationLogInfoEnum.syncThdDsListData.getMessage());
            operationLogInfoService.saveOperationLogInfo(operationLogInfoDTO);
//            resultVO = tdhServicesDsService.syncThdDsListData(tdhDsMonthsDTOS,userCode);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }

    /**
     * 数据同步回复结果
     * @param
     * @return
     */
    @PostMapping(value = "/syncResult")
    public ResultVO syncResult(@Param("result") String result,@Param("id") String id){
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsService.syncResult(result,id);
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
    public  ResultVO countTdhDsauditDataoByAuditStatus(TdhDsDTO tdhDsDTO) {
        logger.info("进入countTdhDsauditDataoByAuditStatuscontroller啦——————————————");
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = tdhServicesDsService.countTdhDsauditDataoByAuditStatus(tdhDsDTO);
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
