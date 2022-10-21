package com.nasr.orderservice.config;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> productServiceCustomizer(){

        return factory -> factory.configure(builder ->
        builder.circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.ofDefaults()),"productService");

    }
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> orderHandlerServiceCustomizer(){

        return factory -> factory.configure(builder ->
        builder.circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.ofDefaults()),"orderHandlerService");
    }
}
