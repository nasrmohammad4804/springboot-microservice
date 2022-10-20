package com.nasr.apigateway.fallback;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order-service-fallback")
public class OrderFallbackController {

    @GetMapping
    public Mono<String> orderFallbackService() {
        return Mono.just("<div style='color:red;' >order service is unavailable !!</div>");
    }
}
