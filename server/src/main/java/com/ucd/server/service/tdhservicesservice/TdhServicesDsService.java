package com.ucd.server.service.tdhservicesservice;



import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsMonthsDTO;

import java.util.List;

public interface TdhServicesDsService {


    /**
     * 点亮“数据同步”按钮（可用状态）
     * @param centre
     * @return
     * @throws Exception
     */
    String updateDataSynchronizationState(String centre) throws Exception;

    PageView getThdServicesDsInfo(PageView pageView, TdhDsDTO tdhSDsDTO) throws Exception;

    ResultVO getThdDsListData(List<TdhDsDTO> tdhDsDTOS) throws Exception;

    ResultVO auditThdDsListData(List<TdhDsMonthsDTO> tdhDsMonthsDTOS) throws Exception;

    PageView getTdhDsMonthsInfo(PageView pageView, TdhDsMonthsDTO tdhDsMonthsDTO) throws Exception;

    ResultVO updateThdDsListData(List<TdhDsMonthsDTO> tdhDsMonthsDTOS) throws Exception;

    ResultVO syncThdDsListData(List<TdhDsMonthsDTO> tdhDsMonthsDTOS) throws Exception;

    ResultVO syncResult(String result, String id)  throws Exception;

    ResultVO countTdhDsauditDataoByAuditStatus(TdhDsDTO tdhDsDTO) throws Exception;
}
