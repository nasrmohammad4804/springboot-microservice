package com.nasr.apigateway.fallback;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/product-service-fallback")
public class ProductFallbackController {

    @GetMapping
    public Mono<String> productFallbackService() {
        return Mono.just("<div style='color:red;' >product service is unavailable !!</div>");
    }
}
