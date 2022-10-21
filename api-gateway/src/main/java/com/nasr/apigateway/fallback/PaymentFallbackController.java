package com.nasr.apigateway.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-service-fallback")
public class PaymentFallbackController {

    @GetMapping
    public ResponseEntity<String> paymentFallbackService(){
        return ResponseEntity.ok(
                "payment service un available !!"
        );
    }
}
