package com.nasr.paymentservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nasr.paymentservice.config.ProjectConfig;
import com.nasr.paymentservice.config.WireMockConfig;
import com.nasr.paymentservice.dto.request.AccountInfo;
import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.nasr.paymentservice.domain.enumeration.PaymentMode.CREDIT_CARD;
import static com.nasr.paymentservice.domain.enumeration.PaymentStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@EnableAutoConfiguration(exclude = {ReactiveOAuth2ClientAutoConfiguration.class})
@ContextConfiguration(classes = {ProjectConfig.class})
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private WireMockConfig wireMockConfig;


    private WireMockServer wireMock;

    @BeforeAll
    void setup() {
        wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().port(wireMockConfig.getPort()));
        wireMock.start();

        completeOrderRequest();
    }

    private void completeOrderRequest() {
        wireMock.stubFor(
                put(urlMatching("/api/v1/order/completeOrderStatus/.*"))
                        .willReturn(ResponseDefinitionBuilder.responseDefinition()
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                .withStatus(OK.value())
                                .withBodyFile("path/json/orderResponse.json"))
        );
    }

    @AfterAll
    void clean() {
        wireMock.stop();
    }

    private void paymentInitializer(PaymentRequest request) {

        webClient.mutateWith(
                        mockJwt().authorities().authorities(
                                List.of(
                                        new SimpleGrantedAuthority("SCOPE_write"),
                                        new SimpleGrantedAuthority("ROLE_USER")
                                )
                        )
                ).post()
                .uri("/api/v1/payment/doPayment")
                .body(Mono.just(request), PaymentRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(PaymentResponse.class)
                .value(response -> assertThat(response).isNotNull());
    }

    @Test
    @DisplayName("this integration test for pay  product ordered by customer ")
    void itShouldDoPayment() {
        // given
        PaymentRequest request = new PaymentRequest(
                CREDIT_CARD, new AccountInfo(6032981441295316L, "431", "12/05"),
                768_000D, 1L
        );
        // when
        //then
        paymentInitializer(request);
    }

    @Test
    @DisplayName("this integration test for get transaction detail by order id")
    void itShouldGetPaymentByOrderId() {
        // given
        PaymentRequest request = new PaymentRequest(
                CREDIT_CARD, new AccountInfo(6037997542109847L, "688", "06/04"),
                438_000D, 2L
        );

        // when
        //then
        paymentInitializer(request);
        webClient.mutateWith(
                    mockJwt().authorities(
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                                    new SimpleGrantedAuthority("SCOPE_read")
                            )
                    )
                )
                .get()
                .uri("/api/v1/payment/{orderId}",2)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(PaymentResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getMode()).isEqualTo(CREDIT_CARD);
                    assertThat(response.getStatus()).isEqualTo(SUCCESS);
                });
    }
}