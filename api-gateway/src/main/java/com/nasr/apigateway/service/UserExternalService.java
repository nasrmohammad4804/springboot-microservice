package com.nasr.apigateway.service;

import com.nasr.apigateway.dto.response.UserInfoResponseDto;
import com.nasr.apigateway.external.response.UserResponseDto;
import reactor.core.publisher.Mono;

public interface UserExternalService {

    Mono<UserInfoResponseDto> getUser(String auth);
}
