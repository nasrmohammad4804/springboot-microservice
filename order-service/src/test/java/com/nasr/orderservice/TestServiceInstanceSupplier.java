package com.nasr.orderservice;


import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceInstanceSupplier implements ServiceInstanceListSupplier {


    private final int port;

    private final String host;

    @Override
    public String getServiceId() {
        return "ORDER-SERVICE";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> serviceInstances = List.of(

                new DefaultServiceInstance("PRODUCT-SERVICE","PRODUCT-SERVICE", host,port,false),
                new DefaultServiceInstance("ORDER_HANDLER-SERVICE","ORDER_HANDLER-SERVICE", host,port,false),
                new DefaultServiceInstance("PAYMENT-SERVICE","PAYMENT-SERVICE", host,port,false)
        );

        return Flux.just(serviceInstances);
    }
}
