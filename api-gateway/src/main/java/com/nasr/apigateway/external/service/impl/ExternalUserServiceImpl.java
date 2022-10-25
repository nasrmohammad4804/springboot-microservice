package com.nasr.apigateway.external.service.impl;

import com.nasr.apigateway.dto.response.UserInfoResponseDto;
import com.nasr.apigateway.external.response.UserResponseDto;
import com.nasr.apigateway.external.service.ExternalUserService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Log4j2
public class ExternalUserServiceImpl implements ExternalUserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    @CircuitBreaker(name = "authorizationServer",fallbackMethod = "authorizationServerFallback")
    @Retry(name = "authorizationServer")
    public Mono<UserInfoResponseDto> getUser(String auth) {

        return webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder.path("/users")
                        .host("AUTHORIZATION-SERVER")
                        .build()
                )
                .header(AUTHORIZATION, auth)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .map(userResponseDto -> {
                    UserInfoResponseDto dto = new UserInfoResponseDto();
                    BeanUtils.copyProperties(userResponseDto,dto);
                    dto.setSsoId(userResponseDto.getId());
                    return dto;
                })
                .log();
    }
    private Mono<UserInfoResponseDto> authorizationServerFallback(String auth, CallNotPermittedException e){
        return authorizationServerFallback(auth, (WebClientResponseException) null);
    }
    private Mono<UserInfoResponseDto> authorizationServerFallback(String auth, WebClientRequestException e){
        return authorizationServerFallback(auth, (WebClientResponseException) null);
    }
    private Mono<UserInfoResponseDto> authorizationServerFallback(String auth, WebClientResponseException e){
        log.error("dont able connect to authorization server for get user info");
        return Mono.just(new UserInfoResponseDto());
    }

}
