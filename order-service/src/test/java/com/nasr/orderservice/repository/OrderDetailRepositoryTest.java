package com.nasr.orderservice.repository;

import com.nasr.orderservice.domain.OrderDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository underTest;


    @BeforeAll
    void setUp() {
        Mono<OrderDetail> orderDetail = underTest.save(new OrderDetail(1L, 2L, 5L));

        StepVerifier.create(orderDetail)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for get all orderDetail by orderId")
    void itShouldFindAllByOrderId() {

        // given
        Long orderId = 1L;

        // when
        Flux<OrderDetail> orderDetails = underTest.findAllByOrderId(orderId);

        //then
        StepVerifier.create(orderDetails)
                .assertNext(order -> {
                    assertThat(order).isNotNull();
                    assertThat(order.getId()).isOne();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for delete orderDetails by orderId")
    void itShouldDeleteAllByOrderId() {

        // given
        Long orderId = 1L;

        // when
        Mono<Void> mono = underTest.deleteAllByOrderId(orderId);

        //then
        StepVerifier.create(mono)
                .verifyComplete();
    }
}