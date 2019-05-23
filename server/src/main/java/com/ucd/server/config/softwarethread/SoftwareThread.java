package com.ucd.server.config.softwarethread;

import org.jboss.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
@Configuration
@EnableAsync
public class SoftwareThread {

    Logger logger = Logger.getLogger(SoftwareThread.class);

        private int corePoolSize = 20;//核心池大小

        private int maxPoolSize = 50;//最大池大小

        private int queueCapacity = 8;

        private int keepAlive = 60;

        @Bean
        public Executor transwarpExecutor() {
            logger.info("+++++++++++++++++++++++++++++++++++++++++++++++corePoolSize"+corePoolSize+"maxPoolSize"+maxPoolSize+"queueCapacity"+queueCapacity+"keepAlive"+keepAlive);
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
