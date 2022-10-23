package com.nasr.orderhandlerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class OrderHandlerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderHandlerServiceApplication.class, args);
    }
}
