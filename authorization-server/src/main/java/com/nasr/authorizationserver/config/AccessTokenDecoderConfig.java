package com.nasr.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class AccessTokenDecoderConfig {

    @Bean
    public AuthenticationManager bearerTokenAuthenticationManager()  {

        JwtDecoder decoder= NimbusJwtDecoder.withPublicKey((RSAPublicKey) KeyPairConfig.getKeyPair().getPublic())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();

        return new ProviderManager(new JwtAuthenticationProvider(decoder));
    }

}
