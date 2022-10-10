package com.nasr.paymentservice.repository;

import com.nasr.paymentservice.domain.Transaction;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction,Long> {

    @Query("select p.* from transaction_table as p where p.order_Id= :orderId ")
    Mono<Transaction> findByOrderId(Long orderId);
}
