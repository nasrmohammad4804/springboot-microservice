package com.nasr.apigateway.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.stream.Collectors;

public class Oauth2TokenUtil {

    public static List<String> extractAuthority(OAuth2User oAuth2User) {
        return ((DefaultOidcUser) oAuth2User).getAuthorities()
                .stream().filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
