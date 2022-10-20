package com.nasr.orderservice.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Objects;

public class Oauth2TokenUtil {


    public static String getAuth(ServerHttpRequest request){

        return Objects.requireNonNull(request.getHeaders()
                        .get("Authorization"))
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("dont extract access token from header of request"));
    }
}
