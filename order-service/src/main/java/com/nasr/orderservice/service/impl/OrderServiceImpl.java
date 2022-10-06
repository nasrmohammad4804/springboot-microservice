package com.nasr.orderservice.service.impl;

import com.nasr.orderservice.base.mapper.BaseMapper;
import com.nasr.orderservice.base.service.impl.BaseServiceImpl;
import com.nasr.orderservice.domain.Order;
import com.nasr.orderservice.domain.enumeration.OrderStatus;
import com.nasr.orderservice.dto.request.*;
import com.nasr.orderservice.dto.response.*;
import com.nasr.orderservice.exception.EntityNotFoundException;
import com.nasr.orderservice.exception.OrderNotFoundException;
import com.nasr.orderservice.exception.OrderNotValidException;
import com.nasr.orderservice.repository.OrderRepository;
import com.nasr.orderservice.service.OrderDetailService;
import com.nasr.orderservice.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static com.nasr.orderservice.constant.ConstantField.ORDER_HANDLER_DEFAULT_HOUR;
import static com.nasr.orderservice.constant.ConstantField.ORDER_HANDLER_GROUP_NAME;

@Service
@Transactional(readOnly = true)
@Log4j2
public class OrderServiceImpl extends BaseServiceImpl<Order, Long, OrderRepository, OrderPlaceResponse, OrderRequest> implements OrderService {


    private final OrderDetailService orderDetailService;

    @Autowired
    private WebClient.Builder webClient;

    public OrderServiceImpl(OrderRepository repository, BaseMapper<Order, OrderPlaceResponse, OrderRequest> mapper, OrderDetailService orderDetailService) {
        super(repository, mapper);
        this.orderDetailService = orderDetailService;
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

    @Override
    @Transactional
    public Mono<OrderPlaceResponse> saveOrUpdate(OrderRequest orderRequest) {
        log.info("placing order request: {} ", orderRequest);

        return decreaseProductQuantity(getDecreaseProductQuantities(orderRequest.getOrderPlaceRequestDtoList()))
                .onErrorMap(e -> new IllegalStateException(e.getMessage()))
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
                }).map(tuples2 -> {

                    OrderPlaceResponse orderPlaceResponseDto = new OrderPlaceResponse();
                    orderPlaceResponseDto.setOrderDate(tuples2.getT2().getOrderDate());
                    orderPlaceResponseDto.setOrderId(tuples2.getT2().getId());
                    tuples2.getT1().forEach(tuple2 -> orderPlaceResponseDto.getProducts()
                            .add(new ProductResponse(tuple2.getProductId(), tuple2.getProductNumber())));

                    return orderPlaceResponseDto;
                })
                .doOnNext(this::createOrderHandler);

//        we place order and reduce quantity of specific product from stock
//        but after that if customer less than 1 hour move to payment and pay the order is ok
//        otherwise we cancelled order and revert  product ordered to stock
//        and after payment is ok we can publish event to shipment  service to tell ready for shipping this order
    }

    private void createOrderHandler(OrderPlaceResponse orderPlaceResponse) {

        TriggerDescriptorRequest triggerDescriptorRequest = TriggerDescriptorRequest.builder()
                .hour(ORDER_HANDLER_DEFAULT_HOUR).build();

        JobDescriptorRequest descriptorRequest = JobDescriptorRequest.builder()
                .orderId(orderPlaceResponse.getOrderId())
                .triggers(List.of(triggerDescriptorRequest))
                .name(UUID.randomUUID().toString())
                .build();

        webClient.build()
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/orderPlaceHandler/groups/" + ORDER_HANDLER_GROUP_NAME + "/jobs")
                        .host("ORDER-HANDLER-SERVICE")
                        .build())
                .body(Mono.just(descriptorRequest), JobDescriptorRequest.class)
                .retrieve()
                .bodyToMono(JobDescriptorRequest.class)
                .subscribe();
    }

    private Mono<Boolean> decreaseProductQuantity(List<DecreaseProductQuantityRequest> decreaseProductQuantityRequests) {
        return webClient.build()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/product/decreaseQuantity")
                        .host("PRODUCT-SERVICE")
                        .build())
                .body(Flux.fromIterable(decreaseProductQuantityRequests), DecreaseProductQuantityRequest.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .log();
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
    public Mono<OrderPlaceWithPaymentResponse> getOrderWithPayment(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("dont find any order with id : " + id)))
                .flatMap(order -> {

                    Flux<OrderDetailResponse> orderDetails = orderDetailService.getOrderDetailsByOrderId(order.getId());
                    Mono<PaymentResponse> payment = getPayment(order.getId());
                    OrderPlaceWithPaymentResponse dto = new OrderPlaceWithPaymentResponse();

                    dto.setOrderId(order.getId());
                    dto.setOrderDate(order.getOrderDate());


                    return orderDetails.collectList()
                            .zipWith(payment)
                            .map(tuple2 -> {
                                dto.setPaymentResponse(tuple2.getT2());
                                dto.setOrderDetails(tuple2.getT1());
                                return dto;
                            });

                });
    }

    private Mono<PaymentResponse> getPayment(Long id) {
        return webClient.build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/payment/" + id)
                        .host("PAYMENT-SERVICE")
                        .build())
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .defaultIfEmpty(new PaymentResponse())
                .log();
    }
}
