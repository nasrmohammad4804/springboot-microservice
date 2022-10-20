package com.nasr.orderservice.service;

import com.nasr.orderservice.base.service.BaseService;
import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderResponse;
import com.nasr.orderservice.external.response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends BaseService<Long, OrderResponse, OrderRequest> {

    Mono<OrderResponse> completeOrderPlacedStatus(Long orderId);

    Flux<ProductResponse> getOrderPlacedProducts(Long orderId,String auth);

    Mono<OrderResponse> placeOrder(OrderRequest orderRequest, String auth);
}
