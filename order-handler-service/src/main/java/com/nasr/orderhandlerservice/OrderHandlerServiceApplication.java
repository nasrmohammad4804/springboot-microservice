package com.nasr.orderhandlerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class OrderHandlerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderHandlerServiceApplication.class, args);
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(ApplicationContext applicationContext) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        factoryBean.setJobFactory(jobFactory);
        factoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        return factoryBean;
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webclient(){
        return WebClient.builder();
    }
}
