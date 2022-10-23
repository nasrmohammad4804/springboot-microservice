package com.nasr.apigateway.service.impl;

import com.nasr.apigateway.dto.response.UserInfoResponseDto;
import com.nasr.apigateway.external.response.UserResponseDto;
import com.nasr.apigateway.service.UserExternalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class UserExternalServiceImpl implements UserExternalService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<UserInfoResponseDto> getUser(String auth) {

        return webClientBuilder.build().get()
//                .uri("http://auth-server:9000/users")
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
}
