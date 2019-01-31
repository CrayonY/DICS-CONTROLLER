package com.ucd.server.service.impl.tdhservicesserviceimpl;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.server.enums.SoftwareExceptEnum;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.trapswapApi.ManagerApi.ServicesApi;
import com.ucd.server.trapswapApi.ManagerApi.UserApi;
import com.ucd.server.trapswapApi.connection.Connection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TdhServicesServiceImplTest {

    @Test
    public void saveThdServicesData() {
        List<TdhServicesInfoDTO> result = new ArrayList<>();
        Connection client = new Connection("http://10.28.3.51:8180/api");
        Date now = new Date();
        try {
            Gson gs = new Gson();
            if (UserApi.login(client, "admin", "admin")) {
                String servicesInfo = ServicesApi.getAllServices(client);
                System.out.println(servicesInfo);
                result = gs.fromJson(servicesInfo,new TypeToken<List<TdhServicesInfoDTO>>(){}.getType());
                System.out.println("1111111111111111111111111111111111111111111111111111111111111111111111111111");
                System.out.println(result);
                UserApi.logout(client);
                client.close();
                if (result == null) {
                    System.out.print("异常：e=" + ResultExceptEnum.ERROR_NOFOUND);
                    throw new SoftwareException(ResultExceptEnum.ERROR_NOFOUND);
                }
                for (TdhServicesInfoDTO tdhServicesInfoDTO:result){
                    String healthChecksId = KeyUtil.genUniqueKey();
                    tdhServicesInfoDTO.setCreattime(now);
                    tdhServicesInfoDTO.setHealthChecksId(healthChecksId);
                    System.out.println("2222222222222222222222222222222222222222222222222222222222222222222");
                    System.out.println(tdhServicesInfoDTO);
                    List<TdhServicesHealthckDTO> tdhServicesHealthckDTOList = tdhServicesInfoDTO.getHealthChecks();
                    if (null == tdhServicesHealthckDTOList || tdhServicesHealthckDTOList.size() ==0){
                        System.out.println("tdhServicesHealthckDTOList为空");
                    }else{
                        for (TdhServicesHealthckDTO tdhServicesHealthckDTO:tdhServicesHealthckDTOList){
                            tdhServicesHealthckDTO.setCreattime(now);
                            System.out.println("333333333333333333333333333333333333333333333333333333333333333333333");
                            System.out.println(tdhServicesHealthckDTO);
                        }
                    }
                }




            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}