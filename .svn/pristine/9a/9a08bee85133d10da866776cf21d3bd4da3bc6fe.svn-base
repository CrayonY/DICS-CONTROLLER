package com.ucd.server.config.configrunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.ucd.client.DaoClient;
import com.ucd.daocommon.DTO.tdhServicesDTO.TdhServicesJobDTO;
import com.ucd.server.mapper.TdhTaskParameterMapper;
import com.ucd.server.model.TdhTaskParameter;
import com.ucd.server.service.tdhservicesservice.TdhServicesjobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;




@Component
@Order(value=1)//boot启动后自启动
public class ConfigRunner implements CommandLineRunner{

	@Value("${basicparameters.configrunning}")
	public String configrunning;
	@Autowired
	public DaoClient daoClient;

	@Autowired
	public TdhTaskParameterMapper tdhTaskParameterMapper;

    @Autowired
    private TdhServicesjobService tdhServicesjobService;
	private final static Logger logger = LoggerFactory.getLogger(ConfigRunner.class);
	/**
	 *
	 */
	public void run(String... args) throws Exception {
		Date now = new Date();
		logger.info("configrunning:"+configrunning+"ConfigRunner 11111111111111111111111111111111111 now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
        if("0".equals(configrunning)){
			logger.info("不需要与对端同步数据！");
		}else{
			logger.info("需要与对端同步数据！");
			//关闭“数据同步”定时任务
			TdhTaskParameter tdhTaskParameter = new TdhTaskParameter();
			tdhTaskParameter.setTaskName("taskServicejob");
			tdhTaskParameter.setTaskStatus(1);
			tdhTaskParameterMapper.updateByTaskName(tdhTaskParameter);
            try {
                boolean flag = tdhServicesjobService.endToEndSynchronizationData();
                if (flag) {
                    logger.info("与对端同步数据成功");
                    //打开“数据同步”定时任务
                    tdhTaskParameter.setTaskStatus(0);
                    tdhTaskParameterMapper.updateByTaskName(tdhTaskParameter);
                }else{
                    logger.info("与对端同步数据失败，请查明原因");
                }
            }catch (Exception e){
                e.printStackTrace();
                logger.info("需要与对端同步数据发生异常！目前不可进行定时任务 e:"+e);
                tdhTaskParameter.setTaskStatus(1);
                tdhTaskParameterMapper.updateByTaskName(tdhTaskParameter);
            }
		}
    }
}