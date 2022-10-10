package com.nasr.orderservice.controller;

import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderResponse;
import com.nasr.orderservice.external.response.ProductResponse;
import com.nasr.orderservice.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public Mono<ResponseEntity<?>> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {

        return orderService.saveOrUpdate(orderRequest)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/cancelOrder/{orderId}")
    public Mono<ResponseEntity<Void>> cancelOrder(@PathVariable Long orderId) {
        return orderService.deleteById(orderId)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/{id}/products",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductResponse> getOrderPlaceProducts(@PathVariable("id") Long orderId){
        return orderService.getOrderPlacedProducts(orderId);
    }

    @PutMapping("/completeOrderStatus/{id}")
    public Mono<OrderResponse> completeOrderPlaceStatus(@PathVariable("id") Long orderId) {
        return orderService.completeOrderPlacedStatus(orderId);
    }

    @GetMapping("/{id}")
    public Mono<OrderResponse> getOrderPlaced(@PathVariable Long id) {
        return orderService.getById(id);
    }

}
