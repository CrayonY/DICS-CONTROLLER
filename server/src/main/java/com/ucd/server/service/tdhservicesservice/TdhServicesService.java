package com.ucd.server.service.tdhservicesservice;


import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;


public interface TdhServicesService {

    //List<TdhServicesInfoDTO> saveThdServicesData() throws Exception;

    String saveThdServicesListData() throws Exception;

    PageView getThdServicesInfo(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) throws Exception;

    /**
     * 点亮“数据同步”按钮（可用状态）
     *
     * @param centre
     * @return
     * @throws Exception
     */
    String updateDataSynchronizationState(String centre) throws Exception;

    /**
     * @return com.ucd.common.utils.pager.PageView
     * @throws
     * @author Crayon
     * @Description 得到实时数据集合
     * @date 2019/4/19 10:06 AM
     * @params [pageView, tdhServicesInfoDTO]
     */
    PageView getThdServicesListNow(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) throws Exception;

    PageView getTdhHealthStatus(PageView pageView, TdhServicesInfoDTO tdhServicesInfoDTO) throws Exception;


    //List<com.ucd.localdaocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO> saveThdServicesLocalDao() throws Exception;
}
