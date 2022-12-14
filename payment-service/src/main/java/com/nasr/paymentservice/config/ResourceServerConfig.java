package com.nasr.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nasr.paymentservice.constant.ConstantField.ROLE_PREFIX;
import static com.nasr.paymentservice.constant.ConstantField.SCOPE_PREFIX;

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
                .oauth2ResourceServer(oauth2 -> oauth2.jwt().jwtAuthenticationConverter(converter()));

        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http.build();
    }

    public Converter<Jwt, Mono<AbstractAuthenticationToken>> converter(){

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> roles =(List<String>) jwt.getClaims().get("roles");
            List<String> scopes =(List<String>) jwt.getClaims().get("scope");

            List<String > authorities  = new ArrayList<>();
            for (String role : roles)
                authorities.add(ROLE_PREFIX.concat(role));

            for (String scope : scopes)
                authorities.add(SCOPE_PREFIX.concat(scope));

            return Flux.fromIterable(authorities.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        });
        return converter;
    }
}
