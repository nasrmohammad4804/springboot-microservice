package com.nasr.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nasr.orderservice.config.ProjectConfig;
import com.nasr.orderservice.config.WireMockConfig;
import com.nasr.orderservice.domain.enumeration.OrderStatus;
import com.nasr.orderservice.dto.request.OrderPlaceRequest;
import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {ReactiveOAuth2ClientAutoConfiguration.class})
@AutoConfigureWebTestClient
@ContextConfiguration(classes = ProjectConfig.class)
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerTest {

    @Autowired
    private WebTestClient webclient;

    @Autowired
    private WireMockConfig wireMock;

    @Autowired
    private CircuitBreakerRegistry registry;

    private WireMockServer wireMockServer;

    private ObjectMapper mapper;

    private OrderResponse orderResponse;

    @AfterAll
    void tearDown() {
        wireMockServer.stop();
    }

    @BeforeAll
    void setup() throws JsonProcessingException {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(wireMock.getPort()));
        wireMockServer.start();

        mapper = new ObjectMapper().findAndRegisterModules()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        decreaseProductQuantityRequest();
        orderHandlerJobRequest();
        getOrderProductRequest();

    }

    private void getOrderProductRequest()  {

        registry.circuitBreaker("productService").reset();

        wireMockServer.stubFor(
                get(urlMatching("/api/v1/product/all.*")).willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE)
                                .withBodyFile("path/json/OrderProductResponses.json"))
        );
    }

    private void orderHandlerJobRequest()  {

        registry.circuitBreaker("orderHandlerService").reset();

        wireMockServer.stubFor(post("/api/v1/orderPlaceHandler/groups/order-handler/jobs")
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("path/json/OrderHandlerJobResponse.json")));
    }

    private String getMockDecreaseProductQuantitiesResult() throws JsonProcessingException {
        return mapper.writeValueAsString(Boolean.TRUE);
    }

    private void decreaseProductQuantityRequest() throws JsonProcessingException {
        registry.circuitBreaker("PRODUCT-SERVICE").reset();

        wireMockServer.stubFor(put("/api/v1/product/decreaseQuantity")
                .willReturn(ResponseDefinitionBuilder.responseDefinition()

                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(getMockDecreaseProductQuantitiesResult())
                ));

    }


    private void orderInitializer(OrderRequest request) {

        EntityExchangeResult<OrderResponse> orderResponseExchangeResult = webclient.mutateWith(mockJwt().authorities(
                        List.of(
                                new SimpleGrantedAuthority("SCOPE_write"),
                                new SimpleGrantedAuthority("ROLE_USER")

                        )
                ))
                .post()
                .uri("/api/v1/order/placeOrder")
                .body(Mono.just(request), OrderRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(OrderResponse.class)
                .value(orderResponse -> {
                    assertThat(orderResponse).isNotNull();
                    assertThat(orderResponse.getOrderStatus()).isEqualTo(OrderStatus.NEW.name());
                })
                .returnResult();

        orderResponse = orderResponseExchangeResult.getResponseBody();
    }


    @Test
    @DisplayName("this integration test for place order with chosen product")
    void itShouldPlaceOrder() {
        // given
        OrderRequest request = new OrderRequest(
                List.of(
                        new OrderPlaceRequest(1L, 3L)

                ), 540_000D
        );

        // when
        //then
        orderInitializer(request);
    }

    @Test
    @DisplayName("this integration test for cancel order after 1 hour if customer dont pay order")
    void itShouldCancelOrder() {

        // given
        OrderRequest request = new OrderRequest(
                List.of(
                        new OrderPlaceRequest(1L, 4L)

                ), 632_000D
        );

        // when
        //then
        orderInitializer(request);

        webclient.mutateWith(
                mockJwt().authorities(
                        List.of(
                                new SimpleGrantedAuthority("SCOPE_write"),
                                new SimpleGrantedAuthority("SCOPE_internal")
                        )
                )
        )
                .delete()
                .uri("/api/v1/order/cancelOrder/{orderId}",orderResponse.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Void.class);
    }

    @Test
    @DisplayName("this integration test for get  product detail of customer order ")
    void itShouldGetOrderPlaceProducts() {
        // given
        OrderRequest request = new OrderRequest(
                List.of(
                        new OrderPlaceRequest(1L, 3L),
                        new OrderPlaceRequest(2L, 2L),
                        new OrderPlaceRequest(3L, 5L)

                ), 540_000D
        );

        // when
        //then
        orderInitializer(request);

        webclient.mutateWith(mockJwt().authorities(
                        List.of(
                                new SimpleGrantedAuthority("SCOPE_read")
                        )
                ))
                .get()
                .uri("/api/v1/order/{id}/products", orderResponse.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentTypeCompatibleWith(TEXT_EVENT_STREAM);

    }

    @Test
    @DisplayName("this integration test for whenever customer pay it order then need to order status change to completed")
    void itShouldCompleteOrderPlaceStatus() {

        // given
        OrderRequest request = new OrderRequest(
                List.of(new OrderPlaceRequest(5L, 4L)),
                128_000D
        );

        // when
        //then
        orderInitializer(request);

        webclient.mutateWith(
                mockJwt().authorities(
                        List.of(
                                new SimpleGrantedAuthority("SCOPE_write"),
                                new SimpleGrantedAuthority("SCOPE_internal")
                        )
                )
        )
                .put()
                .uri("/api/v1/order/completeOrderStatus/{id}",orderResponse.getId())
                .exchange()
                .expectBody(OrderResponse.class)
                .value(response -> assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED.name()));
    }

    @Test
    @DisplayName("this integration test for get information of customer order ")
    void itShouldGetOrderPlaced() {
        // given
        OrderRequest request = new OrderRequest(
                List.of(new OrderPlaceRequest(7L, 3L)),
                87_000D
        );

        // when
        //then
        orderInitializer(request);

        webclient.mutateWith(
                        mockJwt().authorities(
                                Collections.singletonList(new SimpleGrantedAuthority("SCOPE_read"))
                        )
                )
                .get()
                .uri("/api/v1/order/{id}", orderResponse.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(OrderResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(orderResponse.getId());
                });
    }
}