package com.nasr.orderhandlerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange()
                .pathMatchers("/v3/api-docs/**", "/webjars/**")
                .permitAll()
                .pathMatchers(HttpMethod.GET,"/**")
                .hasAuthority("SCOPE_read")
                .pathMatchers(HttpMethod.POST,"/**")
                .hasAuthority("SCOPE_write")
                .pathMatchers(HttpMethod.DELETE,"/**")
                .hasAuthority("SCOPE_write")
                .pathMatchers(HttpMethod.PATCH,"/**")
                .hasAuthority("SCOPE_write")
                .pathMatchers(HttpMethod.PUT,"/**")
                .hasAuthority("SCOPE_write")
                .anyExchange()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();

        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http.build();
    }
}