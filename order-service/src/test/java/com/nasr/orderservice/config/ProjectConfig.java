package com.nasr.orderservice.config;

import com.nasr.orderservice.TestServiceInstanceSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ProjectConfig {

    @Autowired
    private WireMockConfig wiremock;

    @Bean
    public ServiceInstanceListSupplier supplier() {
        return new TestServiceInstanceSupplier(wiremock.getPort(), wiremock.getHost());

    }
}
