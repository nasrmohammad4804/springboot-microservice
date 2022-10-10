package com.nasr.orderhandlerservice.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nasr.orderhandlerservice.model.request.RevertProductRequest;
import com.nasr.orderhandlerservice.model.response.OrderResponse;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String productInfo = jobDataMap.get("productInfo").toString();

        webClient.build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/order/" + orderId)
                        .host("ORDER-SERVICE")
                        .build())
                .retrieve()

                .bodyToMono(OrderResponse.class)
                .log()
                .flatMap(orderResponse -> {

                    log.info("order placed  fetched : "+orderResponse);

                    boolean result = hasPaymentBeenSuccessful(orderResponse);
                    if (!result) {

                        Mono<Boolean> booleanMono = revertProductToStock(productInfo);

                        Mono<Void> voidMono = cancelOrder(orderResponse.getId());
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

    private Mono<Boolean> revertProductToStock(String data) {

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<Long,Long>> typeReference = new TypeReference<>() {};
        try {
            Map<Long, Long> productInfo = mapper.readValue(data, typeReference);
            List<RevertProductRequest> revertProductRequests =new ArrayList<>();

            for (Map.Entry<Long,Long> entry : productInfo.entrySet())
                revertProductRequests.add(new RevertProductRequest(entry.getKey(),entry.getValue()));

            return webClient.build()
                    .put()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/product/revertProduct")
                            .host("PRODUCT-SERVICE")
                            .build())
                    .body(Flux.fromIterable(revertProductRequests), RevertProductRequest.class)
                    .retrieve()
                    .bodyToMono(Boolean.class);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("dont cant parse string to productInfo map");
        }

    }

    private boolean hasPaymentBeenSuccessful(OrderResponse orderResponse) {

        String status = orderResponse.getOrderStatus();
        return status != null && status.equals("COMPLETED");
    }
}
