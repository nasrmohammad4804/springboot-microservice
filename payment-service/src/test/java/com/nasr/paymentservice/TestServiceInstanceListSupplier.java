package com.nasr.paymentservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {

    private final int port;
    private final String host;

    @Override
    public String getServiceId() {
        return "PAYMENT-SERVICE";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {

        List<ServiceInstance> serviceInstances = List.of(
                new DefaultServiceInstance("ORDER-SERVICE","ORDER-SERVICE",host,port,false),
                new DefaultServiceInstance("PRODUCT-SERVICE","PRODUCT-SERVICE",host,port,false),
                new DefaultServiceInstance("ORDER-HANDLER-SERVICE","ORDER-HANDLER-SERVICE",host,port,false)
        );

        return Flux.just(serviceInstances);
    }
}
