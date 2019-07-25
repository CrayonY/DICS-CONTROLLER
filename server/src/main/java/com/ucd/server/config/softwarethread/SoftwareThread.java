package com.ucd.server.config.softwarethread;


import com.ucd.server.config.scheduleconfig.ScheduleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
@Configuration
@EnableAsync
public class SoftwareThread {

    private static final Logger logger = LoggerFactory.getLogger(SoftwareThread.class);

        private int corePoolSize = 20;//核心池大小

        private int maxPoolSize = 50;//最大池大小

        private int queueCapacity = 8;

        private int keepAlive = 60;

        @Bean
        public Executor transwarpExecutor() {

            logger.info("+++++++++++++++++++++++++++++++++++++++++++++++corePoolSize:【{}】,maxPoolSize:【{}】,queueCapacity:【{}】,keepAlive:【{}】",corePoolSize,maxPoolSize,queueCapacity,keepAlive);
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(corePoolSize);
            executor.setMaxPoolSize(maxPoolSize);
            executor.setQueueCapacity(queueCapacity);
            executor.setThreadNamePrefix("transwarpExecutor-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.setKeepAliveSeconds(keepAlive);
            executor.initialize();
            return executor;
        }


}
