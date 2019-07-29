package com.ucd.server.controller.hardwarecontroller2;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.ResultVOUtil;
import com.ucd.common.utils.Tools;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareCpuDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareInfoDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareNowDTO;
import com.ucd.server.enums.TdhServicesReturnEnum;
import com.ucd.server.service.hardwareservice2.HardWareService2;
import com.ucd.server.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *@ClassName: HardwareController
 *@Description: 硬件
 *@Author: gwm
 *@CreateDate: 2019/5/5 11:22 AM
 *@Version 1.0
 *@Copyright:  Copyright©2018-2019 BJCJ Inc. All rights reserved.
 **/
@CrossOrigin
@RestController
@RequestMapping("/hardware2")
public class HardwareController2 {

	@Autowired
	private HardWareService2 hardWareService2;
	/**
	 * 引入日志，注意都是"org.slf4j"包下
	 */
	private final static Logger logger = LoggerFactory.getLogger(HardwareController2.class);

	@PostMapping(value = "/saveHardWareInfo")
	public ResultVO saveHardWareInfo(@RequestBody HardwareInfoDTO hardwareInfoDTO ){
		ResultVO resultVO = new ResultVO();
		logger.info("接受参数2："+hardwareInfoDTO);
		try {
			String result = hardWareService2.saveHardWareInfo(hardwareInfoDTO);
			resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
		} catch (Exception e) {
			e.printStackTrace();
			resultVO = ResultVOUtil.error(e);
		}
		logger.info("resultVO:"+resultVO);
		return resultVO;
	}


	@PostMapping(value = "/gethardwareInfo")
	public ResultVO gethardwareInfo(PageView pageView, HardwareNowDTO hardwareNowDTO){
		logger.info("pageView:"+pageView);
		logger.info("hardwareNowDTO:"+hardwareNowDTO);
		ResultVO resultVO = new ResultVO();
		try {
			if(pageView == null){
				pageView = new PageView();
			}
			pageView = hardWareService2.gethardwareInfo(pageView,hardwareNowDTO);
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

//	@PostMapping(value = "/saveHardWareInfo1")
//	public ResultVO saveHardWareInfo1(@RequestParam(value = "param", required = false)String param ){
//		ResultVO resultVO = new ResultVO();
//		logger.info("接受参数1："+param);
//		HardwareDTO result = Tools.jsonToObject(param,HardwareDTO.class);
//		logger.info("解析参数1："+result);
//		resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
//		logger.info("resultVO:"+resultVO);
//		return resultVO;
//	}

//	@PostMapping(value = "/saveHardWareInfo")
//	public ResultVO saveHardWareInfo(@RequestBody HardwareDTO hardwareDTO ){
//		ResultVO resultVO = new ResultVO();
//		logger.info("接受参数1："+hardwareDTO);
//        try {
//		    String result = hardWareService2.saveHardWareInfo(hardwareDTO);
//		    resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultVO = ResultVOUtil.error(e);
//        }
//		logger.info("resultVO:"+resultVO);
//		return resultVO;
//	}

//	@PostMapping(value = "/gethardwareInfo")
//	public ResultVO gethardwareInfo(PageView pageView, HardwareDTO hardwareDTO){
//		logger.info("pageView:"+pageView);
//		logger.info("hardwareDTO:"+hardwareDTO);
//		ResultVO resultVO = new ResultVO();
//		try {
//			if(pageView == null){
//				pageView = new PageView();
//			}
//			pageView =hardWareService2.gethardwareInfo(pageView,hardwareDTO);
//			resultVO = ResultVOUtil.setResult(TdhServicesReturnEnum.SUCCESS.getCode(),TdhServicesReturnEnum.SUCCESS.getMessage(),pageView);
//			logger.info("resultVO:"+resultVO);
//			return resultVO;
//		} catch (Exception e) {
//			e.printStackTrace();
//			resultVO = ResultVOUtil.error(e);
//			logger.info("resultVO:"+resultVO);
//			return resultVO;
//		}
//	}

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

	/**
	 * @author gwm
	 * @Description 获取所有硬件实时状态信息
	 * @date 2019/4/28 10:42 AM 
	 * @params [host]
	 * @exception  
	 * @return com.ucd.common.VO.ResultVO<java.util.Map<java.lang.String,java.lang.Object>>  
	 */
	@PostMapping(value = "/getHardWareListNow")
	public ResultVO<Map<String, Object>> getHardWareListNow(String host){
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = hardWareService2.getHardWareListNow(host);
			return resultVO;
		} catch (Exception e) {
			e.printStackTrace();
			resultVO = ResultVOUtil.error(e);
			logger.info("resultVO:"+resultVO);
			return resultVO;
		}
	}


	/**
	 * @author gwm
	 * @Description 根据时间区间查看硬件状态
	 * @date 2019/5/5 2:00 PM
	 * @params [host, type]
	 * @exception
	 * @return com.ucd.common.VO.ResultVO<java.util.Map<java.lang.String,java.lang.Object>>
	 */
	@PostMapping(value = "/getHardWareStatusByTime")
	public ResultVO<Map<String,Object>> getHardWareStatusByTime(String type,String nipsOrThreadNames,HardwareCpuDTO hardwareCpuDTO){

		 return hardWareService2.getHardWareStatusByTime(type,nipsOrThreadNames,hardwareCpuDTO);
	}


	/***
	 * @author gongweimin
	 * @Description 获取所有硬件host
	 * @date 2019/6/12 10:16
	 * @params [host]
	 * @exception
	 * @return com.ucd.common.VO.ResultVO<java.util.Map<java.lang.String,java.lang.Object>>
	 */
    @GetMapping(value = "/getHardWareHostList")
    public ResultVO<Map<String, Object>> getHardWareHostList(){
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = hardWareService2.getHardWareHostList();
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO = ResultVOUtil.error(e);
            logger.info("resultVO:"+resultVO);
            return resultVO;
        }
    }
}
