package com.nasr.orderservice.service.impl;

import com.nasr.orderservice.base.mapper.BaseMapper;
import com.nasr.orderservice.base.service.impl.BaseServiceImpl;
import com.nasr.orderservice.domain.OrderDetail;
import com.nasr.orderservice.dto.request.OrderDetailRequest;
import com.nasr.orderservice.dto.response.OrderDetailResponse;
import com.nasr.orderservice.exception.OrderDetailNotFoundException;
import com.nasr.orderservice.exception.OrderNotFoundException;
import com.nasr.orderservice.repository.OrderDetailRepository;
import com.nasr.orderservice.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
public class OrderDetailServiceImpl extends BaseServiceImpl<OrderDetail,Long, OrderDetailRepository, OrderDetailResponse, OrderDetailRequest> implements OrderDetailService {


    public OrderDetailServiceImpl(OrderDetailRepository repository, BaseMapper<OrderDetail, OrderDetailResponse, OrderDetailRequest> mapper) {
        super(repository, mapper);
    }

    @Override
    public Class<OrderDetail> getEntityClass() {
        return OrderDetail.class;
    }

    @Override
    public Flux<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId) {
        return repository.findAllByOrderId(orderId)
                .map(mapper::convertEntityToDto)
                .log();
    }

    @Override
    @Transactional
    public Mono<Void> deleteOrderDetailByOrderId(Long orderId) {
        return repository.deleteAllByOrderId(orderId)
                .log();
    }
}
