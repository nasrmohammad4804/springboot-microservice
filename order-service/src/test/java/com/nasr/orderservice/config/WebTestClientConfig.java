package com.nasr.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebTestClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webclient() {
        return WebClient.builder();
    }
}
