package com.nasr.orderservice.service.impl;

import com.nasr.orderservice.base.mapper.BaseMapper;
import com.nasr.orderservice.base.service.impl.BaseServiceImpl;
import com.nasr.orderservice.domain.Order;
import com.nasr.orderservice.domain.enumeration.OrderStatus;
import com.nasr.orderservice.dto.request.OrderDetailRequest;
import com.nasr.orderservice.dto.request.OrderPlaceRequest;
import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderDetailResponse;
import com.nasr.orderservice.dto.response.OrderResponse;
import com.nasr.orderservice.exception.ErrorResponse;
import com.nasr.orderservice.exception.ExternalServiceException;
import com.nasr.orderservice.exception.OrderNotFoundException;
import com.nasr.orderservice.external.request.DecreaseProductQuantityRequest;
import com.nasr.orderservice.external.request.JobDescriptorRequest;
import com.nasr.orderservice.external.request.TriggerDescriptorRequest;
import com.nasr.orderservice.external.response.ProductResponse;
import com.nasr.orderservice.repository.OrderRepository;
import com.nasr.orderservice.service.OrderDetailService;
import com.nasr.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nasr.orderservice.constant.ConstantField.ORDER_HANDLER_DEFAULT_HOUR;
import static com.nasr.orderservice.constant.ConstantField.ORDER_HANDLER_GROUP_NAME;

@Service
@Transactional(readOnly = true)
@Log4j2
public class OrderServiceImpl extends BaseServiceImpl<Order, Long, OrderRepository, OrderResponse, OrderRequest> implements OrderService {


    private final OrderDetailService orderDetailService;

    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    private final WebClient.Builder webClient;

    public OrderServiceImpl(OrderRepository repository, BaseMapper<Order, OrderResponse, OrderRequest> mapper,
                            OrderDetailService orderDetailService, ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory,
                            WebClient.Builder webClient) {
        super(repository, mapper);
        this.orderDetailService = orderDetailService;
        this.reactiveCircuitBreakerFactory = reactiveCircuitBreakerFactory;
        this.webClient = webClient;
    }

    @Override
    @Transactional
    public Mono<OrderResponse> saveOrUpdate(OrderRequest request) {
        return placeOrder(request);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(Long id) {
        Mono<Void> orderDetail = orderDetailService.deleteOrderDetailByOrderId(id);
        Mono<Void> order = repository.deleteById(id);

        return Flux.concat(orderDetail, order)
                .then();

    }

    @Override
    public Class<Order> getEntityClass() {
        return Order.class;
    }

    @CircuitBreaker(name = "orderHandlerService", fallbackMethod = "orderHandlerServiceFallback")
    private Mono<Object> createOrderHandler(Tuple2<List<OrderDetailResponse>, Order> tuple2) {

        TriggerDescriptorRequest triggerDescriptorRequest = TriggerDescriptorRequest.builder()
                .hour(ORDER_HANDLER_DEFAULT_HOUR).build();

        JobDescriptorRequest descriptorRequest = JobDescriptorRequest.builder()
                .orderId(tuple2.getT2().getId())
                .triggers(List.of(triggerDescriptorRequest))
                .name(UUID.randomUUID().toString())
                .productInfo(getOrderProductDetails(tuple2.getT1()))
                .build();

        return webClient.build()
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/orderPlaceHandler/groups/" + ORDER_HANDLER_GROUP_NAME + "/jobs")
                        .host("ORDER-HANDLER-SERVICE")
                        .build())
                .body(descriptorRequest, JobDescriptorRequest.class)
                .retrieve()
                .onStatus(httpStatus -> (httpStatus.isError() && !HttpStatus.SERVICE_UNAVAILABLE.equals(httpStatus)), clientResponse ->
                        Mono.error(() -> new ExternalServiceException("error occurred for create job on order handler service ", clientResponse.statusCode())))
                .bodyToMono(Object.class)
                .transform(it -> {
                    ReactiveCircuitBreaker circuitBreaker = reactiveCircuitBreakerFactory.create("orderHandlerService");
                    return circuitBreaker.run(it, Mono::error);
                })
                .onErrorMap(customException ->
                        !(customException instanceof ExternalServiceException), error -> orderHandlerServiceFallback())
                .log();
    }

    private Exception orderHandlerServiceFallback() {
        log.error("---------------------- order handler service fallback error ---------------------");
        return new ExternalServiceException(
                "order handler service unAvailable !!!"
                ,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    private Map<Long, Long> getOrderProductDetails(List<OrderDetailResponse> orderDetailResponses) {

        return orderDetailResponses.stream()
                .collect(Collectors.toMap(OrderDetailResponse::getProductId, OrderDetailResponse::getProductNumber));
    }

    private Mono<Object> decreaseProductQuantity(List<DecreaseProductQuantityRequest> decreaseProductQuantityRequests) {
        return webClient.build()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/product/decreaseQuantity")
                        .host("PRODUCT-SERVICE")
                        .build())
                .body(decreaseProductQuantityRequests, DecreaseProductQuantityRequest.class)
                .retrieve()
                .onStatus(httpStatus -> (httpStatus.isError() && !HttpStatus.SERVICE_UNAVAILABLE.equals(httpStatus)),
                        clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                                .map(error -> new ExternalServiceException(error.getMessage(), clientResponse.statusCode())))
                .bodyToMono(Object.class)
                .transform(it -> {
                    ReactiveCircuitBreaker circuitBreaker = reactiveCircuitBreakerFactory.create("productService");
                    return circuitBreaker.run(it, Mono::error);
                })
                .onErrorMap(customException -> !(customException instanceof ExternalServiceException),
                        error -> decreaseProductServiceFallback())
                .log();
    }

    private Exception decreaseProductServiceFallback() {
        log.error("---------------------- product service fallback error ---------------------");
        return new ExternalServiceException(
                "product service unAvailable !!!"
                ,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    private List<DecreaseProductQuantityRequest> getDecreaseProductQuantities(List<OrderPlaceRequest> orderPlaceRequests) {
        List<DecreaseProductQuantityRequest> dtos = new ArrayList<>();
        orderPlaceRequests.forEach(orderDetailResponseDto -> {
            DecreaseProductQuantityRequest dto = new DecreaseProductQuantityRequest();
            BeanUtils.copyProperties(orderDetailResponseDto, dto);
            dtos.add(dto);
        });
        return dtos;
    }

    @Override
    @Transactional
    public Mono<OrderResponse> completeOrderPlacedStatus(Long orderId) {
        return repository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("dont find any order with id : " + orderId)))
                .flatMap(order -> {
                    order.setOrderStatus(OrderStatus.COMPLETED.name());
                    Mono<Order> orderMono = repository.save(order);

                    return orderMono.map(orderEntity -> {
                        OrderResponse orderResponse = new OrderResponse();
                        BeanUtils.copyProperties(orderEntity, orderResponse);
                        return orderResponse;
                    });
                });
    }

    @Override
    public Flux<ProductResponse> getOrderPlacedProducts(Long orderId) {
        Flux<OrderDetailResponse> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);

        return orderDetails.map(OrderDetailResponse::getProductId)
                .collectList()
                .flatMapMany(productIds -> webClient.build()
                        .get()
                        .uri(uriBuilder -> uriBuilder.path("/api/v1/product/all")
                                .host("PRODUCT-SERVICE")
                                .queryParam("id", productIds)
                                .build()
                        )
                        .retrieve()
                        .bodyToFlux(ProductResponse.class)
                        .zipWith(orderDetails))
                .map(tuples2 -> {
                    ProductResponse productResponse = tuples2.getT1();
                    productResponse.setQuantity(tuples2.getT2().getProductNumber());
                    return productResponse;
                });
    }

    @Override
    @Transactional
    public Mono<OrderResponse> placeOrder(OrderRequest orderRequest) {
        log.info("placing order request: {} ", orderRequest);

        return decreaseProductQuantity(getDecreaseProductQuantities(orderRequest.getOrderPlaceRequestDtoList()))
                .flatMap(result -> {
                    Order order = Order.builder()
                            .orderDate(LocalDateTime.now())
                            .totalPrice(orderRequest.getTotalPrice())
                            .orderStatus(OrderStatus.NEW.name())
                            .build();

                    return repository.save(order)
                            .flatMap(o -> {
                                List<OrderDetailRequest> orderDetailRequestDtos = new ArrayList<>();
                                orderRequest.getOrderPlaceRequestDtoList()
                                        .forEach(orderPlace -> orderDetailRequestDtos.add(new OrderDetailRequest(orderPlace.getProductId(), o.getId(), orderPlace.getProductNumber())));
                                return orderDetailService.saveAll(orderDetailRequestDtos)
                                        .collectList().zipWith(Mono.just(o));

                            });

                }).flatMap(tuples2 -> createOrderHandler(tuples2)
                        .map(jobDescriptorRequest -> {
                            log.info("sent job successfully to order handler service");
                            return mapper.convertEntityToDto(tuples2.getT2());
                        }))
                .log();

//        we place order and reduce quantity of specific product from stock
//        but after that if customer less than 1 hour move to payment and pay the order is ok
//        otherwise we cancelled order and revert  product ordered to stock
//        and after payment is ok we can publish event to shipment  service to tell ready for shipping this order
    }
}