package com.nasr.paymentservice.repository;

import com.nasr.paymentservice.domain.Transaction;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Transaction,Long> {

    @Query("select p.id as id , p.payment_mode as mode , p.payment_status as status  from payment_table as p where p.order_Id= :orderId ")
    Mono<PaymentResponse> findByOrderId(Long orderId);
}
