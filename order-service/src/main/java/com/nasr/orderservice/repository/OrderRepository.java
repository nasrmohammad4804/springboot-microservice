package com.nasr.orderservice.repository;

import com.nasr.orderservice.domain.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order,Long> {
}
