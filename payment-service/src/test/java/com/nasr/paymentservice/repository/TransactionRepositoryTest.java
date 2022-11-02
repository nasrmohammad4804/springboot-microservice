package com.nasr.paymentservice.repository;

import com.nasr.paymentservice.domain.Transaction;
import com.nasr.paymentservice.domain.enumeration.PaymentMode;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository underTest;

    private void transactionInitializer(Transaction transaction){
        Mono<Transaction> mono = underTest.save(transaction);

        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for get transaction detail by order id")
    void itShouldFindByOrderId() {

        // given
        Transaction transaction  = Transaction.builder()
                .orderId(1L)
                .mode(PaymentMode.CASH)
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();

        // when
        transactionInitializer(transaction);
        Mono<Transaction> result = underTest.findByOrderId(1L);
        //then

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isNotNull();
                })
                .verifyComplete();

    }
}