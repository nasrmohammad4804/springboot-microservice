package com.nasr.apigateway.controller;

import com.nasr.apigateway.dto.response.TokenInfoResponseDto;
import com.nasr.apigateway.dto.response.UserInfoResponseDto;
import com.nasr.apigateway.external.service.ExternalUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.nasr.apigateway.util.Oauth2TokenUtil.extractAuthority;
import static com.nasr.apigateway.util.Oauth2TokenUtil.getAuth;

@RestController
@Log4j2
public class GatewayLoginController {

    @Autowired
    private ExternalUserService externalUserService;

    @GetMapping("/authenticate")
    public Mono<TokenInfoResponseDto> login(@RegisteredOAuth2AuthorizedClient("ecommerce-gateway") OAuth2AuthorizedClient client,
                                            @AuthenticationPrincipal OidcUser user ) {

        String jwtToken = client.getAccessToken().getTokenValue();
        log.info("access token received from authorization server with value : {}", jwtToken);

        Mono<UserInfoResponseDto> userResponse = externalUserService.getUser(getAuth(jwtToken));

        return userResponse.map(userResponseData -> TokenInfoResponseDto.builder()
                .userInfo(userResponseData)
                .accessToken(client.getAccessToken().getTokenValue())
                .refreshToken(Objects.requireNonNull(client.getRefreshToken()).getTokenValue())
                .accessTokenExpireAt(client.getAccessToken().getExpiresAt())
                .authorities(extractAuthority(user))
                .build());

    }
}
