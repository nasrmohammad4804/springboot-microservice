package com.nasr.orderhandlerservice.job;

import com.nasr.orderhandlerservice.model.enumeration.PaymentStatus;
import com.nasr.orderhandlerservice.model.request.RevertProductRequest;
import com.nasr.orderhandlerservice.model.response.OrderPlaceWithPaymentResponse;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Log4j2
public class OrderPlacedHandlerJob implements Job {

    @Autowired
    private WebClient.Builder webClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext)  {

        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();


        Object orderId = jobDataMap.get("orderId");
        webClient.build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/order/" + orderId)
                        .host("ORDER-SERVICE")
                        .build())
                .retrieve()

                .bodyToMono(OrderPlaceWithPaymentResponse.class)
                .log()
                .flatMap(orderPlaceWithPaymentResponse -> {

                    log.info("order placed with payment fetched : "+orderPlaceWithPaymentResponse);

                    boolean result = hasPaymentBeenSuccessful(orderPlaceWithPaymentResponse);
                    if (!result) {

                        Mono<Boolean> booleanMono = revertProductToStock(orderPlaceWithPaymentResponse);

                        Mono<Void> voidMono = cancelOrder(orderPlaceWithPaymentResponse.getOrderId());
                        return booleanMono.zipWith(voidMono);
                    }
                    log.info("payment with order id : {} was successfully",orderId);
                    return Mono.empty();
                })
                .subscribe(data -> log.info("order with id : {} successfully reverted !! ",orderId));
    }

    private Mono<Void> cancelOrder(Long orderId) {

        return webClient.build().delete()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/order/cancelOrder/" + orderId)
                        .host("ORDER-SERVICE")
                        .build())
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<Boolean> revertProductToStock(OrderPlaceWithPaymentResponse orderPlaceWithPaymentResponse) {

        List<RevertProductRequest> revertProducts = orderPlaceWithPaymentResponse.getOrderDetails()
                .stream().map(orderDetailResponse -> {
                    RevertProductRequest revertProductRequest = new RevertProductRequest();
                    BeanUtils.copyProperties(orderDetailResponse, revertProductRequest);
                    return revertProductRequest;
                }).toList();

        return webClient.build()
                .put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/product/revertProduct")
                        .host("PRODUCT-SERVICE")
                        .build())
                .body(Flux.fromIterable(revertProducts), RevertProductRequest.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    private boolean hasPaymentBeenSuccessful(OrderPlaceWithPaymentResponse orderPlaceWithPaymentResponse) {


        PaymentStatus status = orderPlaceWithPaymentResponse.getPaymentResponse().getStatus();
        return status != null && status.equals(PaymentStatus.SUCCESS);
    }
}
