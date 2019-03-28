package com.ucd.server.controller.operationLogInfocontroller;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.operationLogInfoDTO.OperationLogInfoDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.operationloginfoservice.OperationLogInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/operation")
public class OperationLogInfoController {

	@Autowired
	private OperationLogInfoService operationLogInfoService;
	/**
	 * 引入日志，注意都是"org.slf4j"包下
	 */
	private final static Logger logger = LoggerFactory.getLogger(OperationLogInfoController.class);



	@PostMapping(value = "/getOperationLogInfo")
	public ResultVO getOperationLogInfo(PageView pageView, OperationLogInfoDTO operationLogInfoDTO){
		logger.info("pageView:"+pageView);
		logger.info("operationLogInfoDTO:"+operationLogInfoDTO);
		ResultVO resultVO = new ResultVO();
		try {
			if(pageView == null){
				pageView = new PageView();
			}
			pageView =operationLogInfoService.getOperationLogInfo(pageView,operationLogInfoDTO);
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


}
