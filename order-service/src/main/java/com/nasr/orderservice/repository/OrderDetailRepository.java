package com.nasr.orderservice.repository;

import com.nasr.orderservice.domain.OrderDetail;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderDetailRepository extends ReactiveCrudRepository<OrderDetail,Long> {

    Flux<OrderDetail> findAllByOrderId(Long orderId);

    @Modifying
    Mono<Void> deleteAllByOrderId(Long orderId);
}
