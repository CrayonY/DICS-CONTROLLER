package com.ucd.server.service.tdhmetricservice;


import com.ucd.daocommon.DTO.tdhmetricDTO.TdhMetricDTO;

public interface RestApiResultService {

	/**
	 * <p>Title: selectUser</p>  
	 * @author lx  
	 * @date  
	 * @return String
	 */
	TdhMetricDTO changeFormatByresult(String result);

}