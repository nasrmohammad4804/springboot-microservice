package com.nasr.orderservice.controller;

import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderResponse;
import com.nasr.orderservice.exception.ExternalServiceException;
import com.nasr.orderservice.external.response.ProductResponse;
import com.nasr.orderservice.service.OrderService;
import com.nasr.orderservice.util.Oauth2TokenUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.nasr.orderservice.util.Oauth2TokenUtil.getAuth;

@Log4j2
@RestController
@RequestMapping("/api/v1/order")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    @PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<?>> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {

        return orderService.saveOrUpdate(orderRequest)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/cancelOrder/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_internal')")
    public Mono<ResponseEntity<Void>> cancelOrder(@PathVariable Long orderId) {
        return orderService.deleteById(orderId)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/{id}/products",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "productService",fallbackMethod = "productServiceFallback")
    @TimeLimiter(name = "productService")
    @Retry(name = "productService")
    public Flux<ProductResponse> getOrderPlaceProducts(@PathVariable("id") Long orderId){
        return  orderService.getOrderPlacedProducts(orderId);
    }

    // this is fallback method for product service
    public Flux<ProductResponse> productServiceFallback(Long orderId ,Exception e){

        return Flux.error(() -> new ExternalServiceException(
                "product service unAvailable !",HttpStatus.SERVICE_UNAVAILABLE
        ));
    }

    @PutMapping("/completeOrderStatus/{id}")
    @PreAuthorize("hasAuthority('SCOPE_internal')")
    public Mono<OrderResponse> completeOrderPlaceStatus(@PathVariable("id") Long orderId) {
        return orderService.completeOrderPlacedStatus(orderId);
    }

    @GetMapping("/{id}")
    public Mono<OrderResponse> getOrderPlaced(@PathVariable Long id) {
        return orderService.getById(id);
    }

}
