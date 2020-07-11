package com.ucd.server.service.tdhservicesservice;


import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhDsauditDTO.TdhDsauditDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsDTO;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDsMonthsDTO;

import java.util.List;

public interface TdhServicesDsauditService {


    ResultVO saveTdhDsauditInfo(TdhDsauditDTO tdhDsauditDTO) throws Exception;

    ResultVO saveTdhDsauditData(List<TdhDsauditDTO> tdhDsauditDTOList) throws Exception;

    PageView getTdhDsauditInfo(PageView pageView, TdhDsauditDTO tdhDsauditDTO) throws Exception;

    ResultVO getTdhDsauditData(List<TdhDsauditDTO> tdhDsauditDTOList) throws Exception;

    ResultVO auditTdhDsauditListData(List<TdhDsauditDTO> tdhDsauditDTOList, String userCode) throws Exception;

    ResultVO countTdhDsauditDataoByAuditStatus(Integer auditStatus) throws Exception;
}
