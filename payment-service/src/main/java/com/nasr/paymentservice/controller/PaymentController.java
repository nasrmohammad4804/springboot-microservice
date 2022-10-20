package com.nasr.paymentservice.controller;

import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import com.nasr.paymentservice.service.TransactionService;
import com.nasr.paymentservice.util.Oauth2TokenUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/payment")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    @Autowired
    private TransactionService paymentService;

    @PostMapping("/doPayment")
    public Mono<ResponseEntity<?>> doPayment(@RequestBody @Valid PaymentRequest paymentRequest, ServerHttpRequest request) {
        return paymentService.doPayment(paymentRequest, Oauth2TokenUtil.getAuth(request))
                .map(ResponseEntity::ok);
    }
    @GetMapping("/{orderId}")
    public Mono<ResponseEntity<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId){
        return paymentService.getByOrderId(orderId)
                .map(ResponseEntity::ok);
    }
}
