package com.nasr.paymentservice.config;

import com.nasr.paymentservice.TestServiceInstanceListSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class ProjectConfig {

    @Autowired
    private WireMockConfig wireMock;

    @Bean
    public ServiceInstanceListSupplier supplier() {
        return new TestServiceInstanceListSupplier(wireMock.getPort(), wireMock.getHost());
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
