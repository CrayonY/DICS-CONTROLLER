package com.ucd.server.service.impl;


import com.ucd.client.DaoClient;
import com.ucd.daocommon.DTO.tdhdsDTO.TdhDssyncDTO;
import com.ucd.server.mapper.TdhTaskParameterMapper;

import com.ucd.server.utils.RemoteShellExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ServiceSync {

    @Value("${basicparameters.transwarp.syncip}")
    public String syncip;
    @Value("${basicparameters.transwarp.syncusername}")
    public String syncusername;
    @Value("${basicparameters.transwarp.syncpassword}")
    public String syncpassword;
    @Value("${basicparameters.transwarp.syncstartshell}")
    public String syncstartshell;
    @Value("${basicparameters.transwarp.syncstopshell}")
    public String syncstopshell;


    private final static Logger logger = LoggerFactory.getLogger(ServiceSync.class);

    /**
     * @return java.lang.String
     * @throws
     * @author gongweimin
     * @Description
     * @date 2019/5/22 15:46
     * @params []
     */
    public String SyncThdListDataThread() throws Exception {
        logger.info("开始调取同步脚本");
        RemoteShellExecutor executor = new RemoteShellExecutor(syncip, syncusername, syncpassword);
        int in = executor.exec(syncstartshell);
        logger.info("数据同步脚本返回in：" + in);
        if (in == 0) {
            return "OK";
        } else {
            return "ERROR";
        }

    }

    /**
     * @return java.lang.String
     * @throws
     * @author gongweimin
     * @Description
     * @date 2019/5/22 15:48
     * @params []
     */
    public String KillSyncDataThread() throws Exception {
        logger.info("开始调取杀死同步进程脚本");
        RemoteShellExecutor executor = new RemoteShellExecutor(syncip, syncusername, syncpassword);
        int in = executor.exec(syncstopshell);
        if (in == 0) {
            return "OK";
        } else {
            return "ERROR";
        }
    }


    public static void main(String[] args) {
    }
}
