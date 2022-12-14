package com.nasr.paymentservice.service.impl;

import com.nasr.paymentservice.base.service.impl.BaseServiceImpl;
import com.nasr.paymentservice.domain.Transaction;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import com.nasr.paymentservice.exception.ErrorResponse;
import com.nasr.paymentservice.exception.ExternalServiceException;
import com.nasr.paymentservice.exception.InvalidPaymentException;
import com.nasr.paymentservice.external.response.OrderResponse;
import com.nasr.paymentservice.mapper.PaymentMapper;
import com.nasr.paymentservice.repository.TransactionRepository;
import com.nasr.paymentservice.service.PaymentService;
import com.nasr.paymentservice.service.TransactionService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
@Transactional(readOnly = true)
@Log4j2
public class TransactionServiceImpl extends BaseServiceImpl<Transaction, Long, TransactionRepository, PaymentResponse, PaymentRequest>
        implements TransactionService {

    private final PaymentService paymentService;
    private final WebClient.Builder webClientBuilder;

    public TransactionServiceImpl(TransactionRepository repository, PaymentMapper mapper, PaymentService paymentService, WebClient.Builder webClientBuilder) {
        super(repository, mapper);
        this.paymentService = paymentService;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Class<Transaction> getEntityClass() {
        return Transaction.class;
    }


    private Mono<OrderResponse> completeOrder(Long orderId) {

        return webClientBuilder.build()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/order/completeOrderStatus/" + orderId)
                        .host("ORDER-SERVICE")
                        .build())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError() && !httpStatus.equals(SERVICE_UNAVAILABLE),clientResponse ->  clientResponse.bodyToMono(ErrorResponse.class)
                        .map(errorResponse -> new ExternalServiceException(errorResponse.message(),clientResponse.statusCode())))
                .bodyToMono(OrderResponse.class)
                .log();
    }

    @Override
    public Mono<PaymentResponse> getByOrderId(Long orderId) {

        return repository.findByOrderId(orderId)
                .map(mapper::convertEntityToDto);
    }

    @Override
    @CircuitBreaker(name = "orderService",fallbackMethod = "orderServiceFallback")
    public Mono<PaymentResponse> doPayment(PaymentRequest paymentRequest) {
        // can pay order by third party service with cardNumber and cvv2 etc ...  we mocked this section
        //and if third party do payment and send response as ok with set paymentStatus as SUCCESS
        //after that we save transaction in db

        PaymentStatus status = null;
        try {
            paymentService.doPayment(paymentRequest);

            status = PaymentStatus.SUCCESS;

        } catch (Exception e) {

            status = PaymentStatus.FAIL;
            log.error("payment was not successfully !");
            throw new InvalidPaymentException(e.getMessage());
        }

        Transaction transaction = mapper.convertViewToEntity(paymentRequest);

        transaction.setStatus(status);
        transaction.setPaymentDate(LocalDateTime.now());

        return completeOrder(paymentRequest.getOrderId())
                .flatMap(orderResponse -> repository.save(transaction))
                .map(mapper::convertEntityToDto)
                .doOnNext(tx -> log.info("payment was successfully and transaction id is : {} ", tx.getId()))
                .log();
    }

    private Mono<PaymentResponse> orderServiceFallback(PaymentRequest paymentRequest, CallNotPermittedException ex){


        log.info("order service unAvailable !!!");
        return Mono.error(() -> new ExternalServiceException("order service un available !!!",SERVICE_UNAVAILABLE));
    }
    private Mono<PaymentResponse> orderServiceFallback(PaymentRequest paymentRequest ,WebClientResponseException ex){
        CallNotPermittedException callNotPermittedException = null;
        return orderServiceFallback(paymentRequest,callNotPermittedException);
    }
    private Mono<PaymentResponse> orderServiceFallback(PaymentRequest paymentRequest , WebClientRequestException ex){
        CallNotPermittedException callNotPermittedException = null;
        return orderServiceFallback(paymentRequest,callNotPermittedException);
    }
}
