package com.nasr.orderservice.service;

import com.nasr.orderservice.base.service.BaseService;
import com.nasr.orderservice.dto.request.OrderDetailRequest;
import com.nasr.orderservice.dto.response.OrderDetailResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderDetailService extends BaseService<Long, OrderDetailResponse, OrderDetailRequest> {

    Flux<OrderDetailResponse > getOrderDetailsByOrderId(Long orderId);

    Mono<Void > deleteOrderDetailByOrderId(Long orderId);
}
