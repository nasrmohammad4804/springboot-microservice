package com.nasr.orderservice.service.impl;

import com.nasr.orderservice.domain.OrderDetail;
import com.nasr.orderservice.dto.response.OrderDetailResponse;
import com.nasr.orderservice.exception.OrderNotFoundException;
import com.nasr.orderservice.mapper.OrderDetailMapper;
import com.nasr.orderservice.repository.OrderDetailRepository;
import com.nasr.orderservice.service.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @Mock
    private OrderDetailRepository repository;

    @Mock
    private OrderDetailMapper mapper;


    private OrderDetailService underTest ;

    @BeforeEach
    void setUp() {
        underTest = new OrderDetailServiceImpl(repository,mapper);
    }

    @Test
    void itShouldGetOrderDetailsByOrderId() {
        // given
        Long orderId = 1L;
        // when
        given(repository.findAllByOrderId(orderId)).willReturn(getMockOrderDetails());

        given(mapper.convertEntityToDto(any()))
                .willReturn(new OrderDetailResponse(2L,1L,4L))
                .willReturn(new OrderDetailResponse(3L,1L,2L))
                .willReturn(new OrderDetailResponse(8L,1L,6L));

        Flux<OrderDetailResponse> orderDetails = underTest.getOrderDetailsByOrderId(orderId);

        //then
        StepVerifier.create(orderDetails.collectList())
                .assertNext(orderDetailsResponse -> assertThat(orderDetailsResponse).hasSize(3))
                .verifyComplete();


    }@Test
    void itShouldThrowNotFoundExceptionOnGetOrderDetailsByOrderIdWhenOrderIdNotValid() {

        // given
        Long orderId = 1L;

        // when
        given(repository.findAllByOrderId(orderId))
                .willReturn(Flux.error(new OrderNotFoundException("dont find any products with orderId : "+orderId)));

        Flux<OrderDetailResponse> orderDetails = underTest.getOrderDetailsByOrderId(orderId);

        //then
        StepVerifier.create(orderDetails.collectList())
                .expectError(OrderNotFoundException.class)
                .verify();

        verify(mapper,never()).convertEntityToDto(any());

    }

    private Flux<OrderDetail> getMockOrderDetails() {
        return Flux.fromStream(
                Stream.of(
                        new OrderDetail(1L, 2L, 4L),
                        new OrderDetail(1L, 3L, 2L),
                        new OrderDetail(1L, 8L, 6L)
                )
        );
    }

    @Test
    void itShouldDeleteOrderDetailByOrderId() {
        // given
        Long orderId = 1L;

        // when
        when(repository.deleteAllByOrderId(orderId)).thenReturn(Mono.empty());

        Mono<Void> mono = underTest.deleteOrderDetailByOrderId(orderId);

        //then
        StepVerifier.create(mono)
                .verifyComplete();
    }
}