package com.nasr.orderservice.service;

import com.nasr.orderservice.base.service.BaseService;
import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderPlaceResponse;
import com.nasr.orderservice.dto.response.OrderPlaceWithPaymentResponse;
import com.nasr.orderservice.dto.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface OrderService extends BaseService<Long, OrderPlaceResponse, OrderRequest> {
    Mono<OrderPlaceWithPaymentResponse> getOrderWithPayment(Long id);

    Mono<OrderResponse> completeOrderPlacedStatus(Long orderId);
}
