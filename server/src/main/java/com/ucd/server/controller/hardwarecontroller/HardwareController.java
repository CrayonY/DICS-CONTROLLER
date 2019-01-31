package com.ucd.server.controller.hardwarecontroller;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.hardwareservice.HardWareService;
import com.ucd.server.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/hardware")
public class HardwareController {

	@Autowired
	private HardWareService hardWareService;
	/**
	 * 引入日志，注意都是"org.slf4j"包下
	 */
	private final static Logger logger = LoggerFactory.getLogger(HardwareController.class);

	@PostMapping(value = "/saveHardWareInfo1")
	public ResultVO saveHardWareInfo1(@RequestParam(value = "param", required = false)String param ){
		ResultVO resultVO = new ResultVO();
		logger.info("接受参数1："+param);
		HardwareDTO result = Tools.jsonToObject(param,HardwareDTO.class);
		logger.info("解析参数1："+result);
		resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
		logger.info("resultVO:"+resultVO);
		return resultVO;
	}

	@PostMapping(value = "/saveHardWareInfo")
	public ResultVO saveHardWareInfo(@RequestBody HardwareDTO hardwareDTO ){
		ResultVO resultVO = new ResultVO();
		logger.info("接受参数1："+hardwareDTO);
        try {
		    String result = hardWareService.saveHardWareInfo(hardwareDTO);
		    resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
        }
		logger.info("resultVO:"+resultVO);
		return resultVO;
	}

	@PostMapping(value = "/gethardwareInfo")
	public ResultVO gethardwareInfo(PageView pageView, HardwareDTO hardwareDTO){
		logger.info("pageView:"+pageView);
		logger.info("hardwareDTO:"+hardwareDTO);
		ResultVO resultVO = new ResultVO();
		try {
			if(pageView == null){
				pageView = new PageView();
			}
			pageView =hardWareService.gethardwareInfo(pageView,hardwareDTO);
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

	@PostMapping(value = "/saveHardWareInfo2")
	public ResultVO saveHardWareInfo2(@RequestBody String param ){
		ResultVO resultVO = new ResultVO();
		logger.info("接受参数2："+param);
		resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),param);
		logger.info("resultVO:"+resultVO);
		return resultVO;
	}

	@GetMapping(value = "/test")
	public ResultVO test(){
		ResultVO resultVO = new ResultVO();
//		String content = "param={\"cpu\":\"1000\",\"vcpu\":\"200\"}";
		String content1 = "cpu="+"1000"+"&"+"vcpu="+"200";
		try {
//			String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo1",content,
//					"application/x-www-form-urlencoded",null);
			String result1 = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/gethardwareInfo",content1,
					"application/x-www-form-urlencoded",null);
			resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result1);
		} catch (Exception e) {
			e.printStackTrace();
			resultVO = ResultVOUtil.error(e);
		}
		return resultVO;
	}



	@GetMapping(value = "/test1")
	public ResultVO test1(){
		ResultVO resultVO = new ResultVO();
		String content = "{\"cpu\":\"1000\",\"vcpu\":\"200\"}";
		HardwareDTO hardwareDTO = new HardwareDTO();
		hardwareDTO.setCpu(5000.0);
		hardwareDTO.setHost("12345687");
		hardwareDTO.setVcpu(499.0);
		List<Double> doubleList = new ArrayList<Double>();
		doubleList.add(10.90);
		doubleList.add(67.99);
		hardwareDTO.setDiskstatus(doubleList);
		hardwareDTO.setNip("ceshi");
		Date now = new Date();
		hardwareDTO.setIntime(String.valueOf(now.getTime()));
		System.out.println(Tools.toJson(hardwareDTO));
		try {
			//String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo",content,"application/json",null);
			String result = HttpClientUtils.postString("http://10.66.1.192:28070/hardware/saveHardWareInfo",Tools.toJson(hardwareDTO), "application/json",null);
			resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
		} catch (Exception e) {
			e.printStackTrace();
			resultVO = ResultVOUtil.error(e);
		}

		return resultVO;
	}


}
