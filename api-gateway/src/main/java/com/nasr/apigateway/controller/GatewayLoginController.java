package com.nasr.apigateway.controller;

import com.nasr.apigateway.dto.response.TokenInfoResponse;
import com.nasr.apigateway.util.Oauth2TokenUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@Log4j2
public class GatewayLoginController {

    @GetMapping("/authenticate")
    public Mono<TokenInfoResponse> login(@RegisteredOAuth2AuthorizedClient("ecommerce-gateway") OAuth2AuthorizedClient client,
                                         @AuthenticationPrincipal OAuth2User oAuth2User , Authentication authentication){

        log.info("access token received from authorization server with value : {}",client.getAccessToken().getTokenValue());

        return Mono.just(
                TokenInfoResponse.builder()
                        .userName(oAuth2User.getName())
                        .accessTokenExpireAt(client.getAccessToken().getExpiresAt())
                        .refreshToken(Objects.requireNonNull(client.getRefreshToken()).getTokenValue())
                        .accessToken(client.getAccessToken().getTokenValue())
                        .authorities(Oauth2TokenUtil.extractAuthority(oAuth2User))
                        .build()
        );
    }
}
