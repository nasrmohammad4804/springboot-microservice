package com.nasr.paymentservice.service;

import com.nasr.paymentservice.base.service.BaseService;
import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface PaymentService extends BaseService<Long, PaymentResponse, PaymentRequest> {

    Mono<PaymentResponse> getByOrderId(Long orderId);
}
