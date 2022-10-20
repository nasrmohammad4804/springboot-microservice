package com.nasr.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean(name = "userKeyResolver")
//    with this key we can keep track of all user rate limiter information
//    for example we store information with user1 have 3 request per second
//    we usually use principal name of session - because there are difference for every use
//    and that we able to add restriction of rate limiter base on every user for example
//
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just("userKey");
    }

}
