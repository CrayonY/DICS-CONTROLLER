package com.ucd.server.service.tdhmetricservice;


import com.ucd.daocommon.DTO.tdhmetricDTO.TdhMetricDTO;

public interface RestApiResultService {

    /**
     * <p>Title: selectUser</p>
     *
     * @return String
     * @author lx
     * @date
     */
    TdhMetricDTO changeFormatByresult(String result);

}