package com.nasr.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFluxSecurity
public class ResourceServerConfig {


    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http) {

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange()
                .pathMatchers("/v3/api-docs/**", "/webjars/**","/actuator/**")
                .permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }
}
