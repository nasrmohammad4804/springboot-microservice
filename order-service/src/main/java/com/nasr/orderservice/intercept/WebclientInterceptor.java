package com.nasr.orderservice.intercept;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import static com.nasr.orderservice.constant.ConstantField.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public interface WebclientInterceptor {

    static ExchangeFilterFunction interceptor(String token) {
        return ExchangeFilterFunction.ofRequestProcessor(request ->
                Mono.just(ClientRequest.from(request)
                        .header(AUTHORIZATION, TOKEN_PREFIX.concat(token)).build()));
    }
}
