package com.nasr.apigateway.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.stream.Collectors;

import static com.nasr.apigateway.constant.ConstantField.TOKEN_PREFIX;

public class Oauth2TokenUtil {

    public static List<String> extractAuthority(OAuth2User oAuth2User) {
        return oAuth2User.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.toList());
    }
    public static String getAuth(String token){
        return TOKEN_PREFIX.concat(token);
    }
}
