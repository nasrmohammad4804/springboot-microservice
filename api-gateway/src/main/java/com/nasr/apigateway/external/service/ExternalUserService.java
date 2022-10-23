package com.nasr.apigateway.external.service;

import com.nasr.apigateway.dto.response.UserInfoResponseDto;
import reactor.core.publisher.Mono;

public interface ExternalUserService {

    Mono<UserInfoResponseDto> getUser(String auth);
}
