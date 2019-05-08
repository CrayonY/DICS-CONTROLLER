package com.ucd.server.service.impl.tdhservicesserviceimpl;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.ucd.common.enums.ResultExceptEnum;
import com.ucd.common.utils.KeyUtil;
import com.ucd.common.utils.pager.PageView;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesHealthckDTO;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesInfoDTO;
import com.ucd.server.exception.SoftwareException;
import com.ucd.server.service.impl.ServiceThread;
import com.ucd.server.service.tdhservicesservice.TdhServicesService;
import com.ucd.server.trapswapApi.ManagerApi.ServicesApi;
import com.ucd.server.trapswapApi.ManagerApi.UserApi;
import com.ucd.server.trapswapApi.connection.Connection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TdhServicesServiceImplTest {

    @Autowired
    private TdhServicesService tdhServicesService;

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



    /**
     * @author Crayon
     * @Description 按时间段查看健康数据       
     * @date 2019/4/22 3:47 PM 
     * @params []
     * @exception  
     * @return void  
     */
    @Test
    public void getTdhHealthStatusTest(){

        PageView pageView = new PageView();
        TdhServicesInfoDTO tdhServicesInfoDTO = new TdhServicesInfoDTO();
        tdhServicesInfoDTO.setSecond(30);
        tdhServicesInfoDTO.setTaskTimeStart("2008-08-10 05:50:00");
        tdhServicesInfoDTO.setCentre("A");
        tdhServicesInfoDTO.setTaskTimeEnd("2008-08-11 10:57:21");
        tdhServicesInfoDTO.setType("TOS");


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime taskTimeStart1 = LocalDateTime.parse("2008-08-10 05:50:11", dtf);
        LocalDateTime taskTimeEnd1 = LocalDateTime.parse("2008-08-11 10:57:21", dtf);

        LocalDate aa = taskTimeStart1.toLocalDate();
        Date newDate = java.sql.Date.valueOf(String.valueOf(taskTimeStart1));
        Date newDate1 = java.sql.Date.valueOf(String.valueOf(taskTimeEnd1));



        try {
            tdhServicesService.getTdhHealthStatus(pageView,tdhServicesInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @author Crayon
     * @Description  查看所有实时表数据
     * @date 2019/4/22 4:18 PM
     * @params []
     * @exception
     * @return com.ucd.common.utils.pager.PageView
     */
    @Test
    public void getThdServicesListNow(){
        PageView pageView = new PageView();
        TdhServicesInfoDTO tdhServicesInfoDTO = new TdhServicesInfoDTO();
        tdhServicesInfoDTO.setCentre("A");

        ServiceThread serviceThread = new ServiceThread();
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(ldt);
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String format = ldt.format(dtf1);
        try {
            pageView = tdhServicesService.getThdServicesListNow(pageView,tdhServicesInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("获取数据内容为"+pageView.toString());

    }


}