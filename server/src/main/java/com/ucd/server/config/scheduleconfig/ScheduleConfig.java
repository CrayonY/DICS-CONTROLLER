package com.ucd.server.config.scheduleconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @ClassName: ScheduleConfig
 * @Description: TODO
 * @Author: gongweimin
 * @CreateDate: 2019/6/10 16:55
 * @Version 1.0
 * @Copyright: Copyright2018-2019 BJCJ Inc. All rights reserved.
 **/
@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    private static final int threadCount = 10;

//    public static final long jobFixedDelay = 20 * 1000L;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(newScheduledExecutorService());
        logger.info("set spring schedule executor service");
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService newScheduledExecutorService() {
        logger.info("Schedule work thread count is {}", threadCount);
        return Executors.newScheduledThreadPool(threadCount);
    }
}
