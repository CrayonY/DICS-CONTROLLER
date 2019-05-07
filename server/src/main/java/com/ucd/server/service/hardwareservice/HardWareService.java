package com.ucd.server.service.hardwareservice;

import com.ucd.common.VO.ResultVO;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareCpuDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareInfoDTO;
import com.ucd.daocommon.DTO.hardwareDTO.HardwareNowDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface HardWareService {
//    public String saveHardWareInfo(HardwareDTO hardwareDTO) throws Exception;

    public String saveHardWareInfo(HardwareInfoDTO hardwareInfoDTO) throws Exception;

//    PageView gethardwareInfo(PageView pageView, HardwareDTO hardwareDTO) throws Exception;

    PageView gethardwareInfo(PageView pageView, HardwareNowDTO hardwareNowDTO) throws Exception;

    /**
     * @author Crayon
     * @Description 查看所有硬件实时状态数据
     * @date 2019/4/28 1:54 PM
     * @params [host]
     * @exception
     * @return com.ucd.common.VO.ResultVO<java.util.Map<java.lang.String,java.lang.Object>>
     */
    ResultVO<Map<String, Object>> getHardWareListNow(String host);

   /**
    * @author Crayon
    * @Description 根据时间区间查看硬件状态
    * @date 2019/5/5 2:14 PM
    * @params [type, hardwareCpuDTO]
    * @exception
    * @return com.ucd.common.VO.ResultVO<java.util.Map<java.lang.String,java.lang.Object>>
    */
    ResultVO<Map<String,Object>> getHardWareStatusByTime(String type, String nipsOrThreadNames,HardwareCpuDTO hardwareCpuDTO);


}
