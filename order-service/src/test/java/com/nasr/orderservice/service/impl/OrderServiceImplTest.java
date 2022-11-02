package com.nasr.orderservice.service.impl;

import com.nasr.orderservice.domain.Order;
import com.nasr.orderservice.domain.enumeration.OrderStatus;
import com.nasr.orderservice.dto.request.OrderPlaceRequest;
import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderDetailResponse;
import com.nasr.orderservice.dto.response.OrderResponse;
import com.nasr.orderservice.exception.EntityNotFoundException;
import com.nasr.orderservice.exception.OrderNotFoundException;
import com.nasr.orderservice.external.request.DecreaseProductQuantityRequest;
import com.nasr.orderservice.external.request.JobDescriptorRequest;
import com.nasr.orderservice.external.request.TriggerDescriptorRequest;
import com.nasr.orderservice.external.response.ProductResponse;
import com.nasr.orderservice.mapper.OrderMapper;
import com.nasr.orderservice.repository.OrderRepository;
import com.nasr.orderservice.service.OrderDetailService;
import com.nasr.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderDetailService orderDetailService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ReactiveCircuitBreakerFactory circuitBreakerFactory;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient.Builder webClient;

    private OrderService underTest;



    @BeforeEach
    void setup() {
        underTest = new OrderServiceImpl(
                repository, mapper, orderDetailService, circuitBreakerFactory, webClient
        );
    }

    @Test
    @DisplayName("this unit test for delete order by specific id when ever order cancelled")
    void itShouldDeleteById() {

        // given
        final Long orderId = 2L;

        // when

        given(orderDetailService.deleteOrderDetailByOrderId(any()))
                .willReturn(Mono.empty());

        given(repository.deleteById(orderId))
                .willReturn(Mono.empty());

        ArgumentCaptor<Long> orderIdCapture = ArgumentCaptor.forClass(Long.class);
        Mono<Void> mono = underTest.deleteById(orderId);

        //then
        then(repository).should(Mockito.only()).deleteById(orderIdCapture.capture());

        StepVerifier.create(mono)
                .verifyComplete();

        assertThat(orderId).isEqualTo(orderIdCapture.getValue());
    }

    @Test
    @DisplayName("this unit for check get error when want to delete order but order id is null")
    void itShouldNotDeleteByIdAndThrowExceptionWhenIdIsNull() {

        // given
        final Long orderId = null;

        // when
        given(orderDetailService.deleteOrderDetailByOrderId(any()))
                .willReturn(Mono.empty());

        when(repository.deleteById(orderId))
                .thenReturn(Mono.error(new IllegalArgumentException("given id must not be null ")));

        Mono<Void> mono = underTest.deleteById(null);

        //then

        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();

        then(repository).should(Mockito.only()).deleteById(orderId);
    }

    @Test
    @DisplayName("this unit test must change order status from NEW to COMPLETED when payment is success")
    void itShouldCompleteOrderPlacedStatus() {

        // given
        Long orderId = 1L;
        Order order = getMockOrder();

        // when
        given(repository.findById(anyLong())).willReturn(Mono.just(order));
        given(repository.save(any())).willReturn(Mono.just(order));

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);

        Mono<OrderResponse> orderResponse = underTest.completeOrderPlacedStatus(orderId);

        //then
        StepVerifier.create(orderResponse)
                .assertNext(response -> assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED.name()))
                .verifyComplete();

        verify(repository).save(orderArgumentCaptor.capture());
        assertThat(orderArgumentCaptor.getValue().getOrderStatus()).isEqualTo(OrderStatus.COMPLETED.name());
    }

    @Test
    @DisplayName("this unit test for handle when we want to complete order but order id is not valid then it will be taken error")
    void itShouldThrowExceptionOnCompleteOrderPlacedStatusWhenOrderIdNotValid() {

        // given
        Long orderId = 1L;

        // when
        given(repository.findById(anyLong())).willReturn(
                Mono.error(new OrderNotFoundException("dont find any order with id : " + orderId))
        );

        Mono<OrderResponse> orderResponse = underTest.completeOrderPlacedStatus(orderId);

        //then
        StepVerifier.create(orderResponse)
                .expectError(EntityNotFoundException.class)
                .verify();

        then(repository).should(never()).save(any());

    }

    private Order getMockOrder() {
        return new Order(1L, LocalDateTime.now(), 415_000D, OrderStatus.NEW.name());
    }

    @Test
    @DisplayName("this unit test for handle when we want to get all ordered product by order id" +
            " but order id not found then expected take not found error ")
    void itShouldNotGetOrderPlacedProductsWhenOrderIdNotExists() {
        // given
        final Long orderId = 1L;

        // when
        given(orderDetailService.getOrderDetailsByOrderId(orderId))
                .willThrow(new OrderNotFoundException("dont find any product with specific order id"));

        //then
        assertThatThrownBy(() -> underTest.getOrderPlacedProducts(orderId).subscribe());

        verifyNoInteractions(webClient);
    }

    @Test
    @DisplayName("this unit test for scenario get ordered product successfully")
    void itShouldGetOrderPlacedProducts() {

        // given
        final Long orderId = 1L;

        // when
        given(orderDetailService.getOrderDetailsByOrderId(orderId))
                .willReturn(Flux.just(
                        new OrderDetailResponse(2L, 1L, 5L),
                        new OrderDetailResponse(3L, 1L, 3L),
                        new OrderDetailResponse(4L, 1L, 2L)
                ));

        given(webClient.build().get().uri(any(Function.class)).retrieve().bodyToFlux(ProductResponse.class))
                .willReturn(Flux.just(
                        new ProductResponse(2L, "iphone", 23L, 36_000_000D),
                        new ProductResponse(3L, "macbook air", 14L, 73_000_000D),
                        new ProductResponse(4L, "smart watch apple v3", 6L, 4_200_000D)
                ));

        Flux<ProductResponse> products = underTest.getOrderPlacedProducts(orderId);

        //then
        StepVerifier.create(products)
                .assertNext(product -> assertThat(product.getName()).isEqualTo("iphone"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for save customer  order place ")
    void itShouldPlaceOrder() {
        // given
        OrderRequest request = getMockOrderPlaceRequest();

        // when
        Mono<Object> decreaseProductQuantitiesResult = Mono.just(Boolean.TRUE);
        given(
                webClient.build().put().uri(any(Function.class)).body(any(Publisher.class), (Class<?>) any(Object.class))
                        .retrieve()
                        .onStatus(any(Predicate.class),any(Function.class))
                        .bodyToMono(Object.class)
        )
                .willReturn(decreaseProductQuantitiesResult);

        lenient().when(circuitBreakerFactory.create(anyString()).run(any(Mono.class), any(Function.class)))
                .thenReturn(Mono.just(Boolean.TRUE));

        Order mockOrder = getMockOrder();
        given(repository.save(any())).willReturn(Mono.just(mockOrder));

        given(orderDetailService.saveAll(anyCollection()))
                .willReturn(getMockOrderDetailResponse());

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        OrderResponse mockOrderResponse = getMockOrderResponse();
        given(mapper.convertEntityToDto(any())).willReturn(mockOrderResponse);

        Mono<OrderResponse> orderResponse = underTest.placeOrder(request);

        //then
        StepVerifier.create(orderResponse)
                .assertNext(response -> assertThat(response.getId()).isEqualTo(1L))
                .verifyComplete();

        verify(repository, times(1)).save(any());
        verify(mapper, times(1)).convertEntityToDto(orderCapture.capture());

        Order captureValue = orderCapture.getValue();
        assertThat(captureValue).isEqualToComparingFieldByField(mockOrder);

    }

    private OrderResponse getMockOrderResponse() {
        return new OrderResponse(1L, LocalDateTime.now(), 415_000D, OrderStatus.NEW.name());
    }

    private Mono<Object> getMockJobDescriptorRequest() {
        return
                Mono.just(
                        JobDescriptorRequest.builder()
                                .orderId(1L)
                                .triggers(List.of(new TriggerDescriptorRequest("test", "group_test", 2)))
                                .build()
                );
    }

    private Flux<OrderDetailResponse> getMockOrderDetailResponse() {
        return Flux.fromStream(
                Stream.of(
                        new OrderDetailResponse(4L, 1L, 5L),
                        new OrderDetailResponse(2L, 1L, 3L)
                )
        );
    }

    private List<DecreaseProductQuantityRequest> getDecreaseProductRequest(OrderRequest request) {
        return request.getOrderPlaceRequestDtoList()
                .stream()
                .map(orderPlaceRequest -> new DecreaseProductQuantityRequest(orderPlaceRequest.getProductId(), orderPlaceRequest.getProductNumber()))
                .collect(Collectors.toList());
    }

    private OrderRequest getMockOrderPlaceRequest() {

        final List<OrderPlaceRequest> orderPlaceRequests = new ArrayList<>();

        orderPlaceRequests.add(new OrderPlaceRequest(1L, 3L));
        orderPlaceRequests.add(new OrderPlaceRequest(2L, 5L));

        return new OrderRequest(orderPlaceRequests, 473_000D);
    }
}