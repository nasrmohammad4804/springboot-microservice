package com.nasr.productservice.controller;

import com.nasr.productservice.dto.request.DecreaseProductQuantityRequest;
import com.nasr.productservice.dto.request.ProductRequest;
import com.nasr.productservice.dto.request.RevertProductRequest;
import com.nasr.productservice.dto.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    private ProductResponse productResponse;


    @Autowired
    private WebTestClient webclient;

    private void  productInitializer(ProductRequest request) {

        WebTestClient.BodySpec<ProductResponse, ?> bodySpec = webclient.post()
                .uri("/api/v1/product")
                .body(Mono.just(request), ProductRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ProductResponse.class)
                .value(response -> assertThat(response.getId()).isNotNull());

        productResponse = bodySpec.returnResult()
                .getResponseBody();

    }

    @Test
    @WithMockUser(username = "user", password = "pass", authorities = {"ROLE_ADMIN", "SCOPE_write"})
    @DisplayName("this integration test for test add product api on system")
    void itShouldAddProduct() {
        // given
        ProductRequest request = new ProductRequest("design pattern book", 34L, 127_000D);

        //then
        // when

        productInitializer(request);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", authorities = {"SCOPE_read", "SCOPE_write", "ROLE_ADMIN"})
    @DisplayName("this integration test for test get all product api on system")
    void itShouldGetProducts() {
        // given
        ProductRequest request = new ProductRequest("saga pattern magazine", 22L, 47_000D);

        // when
        productInitializer(request);

        //then
        webclient.get()
                .uri("/api/v1/product")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(ProductResponse.class)
                .value(productResponse -> assertThat(productResponse.size()).isGreaterThanOrEqualTo(1));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", authorities = {"SCOPE_read", "ROLE_ADMIN", "SCOPE_write"})
    @DisplayName("this integration test for test get product by id api on system")
    void itShouldGetProduct() {
        // given
        ProductRequest request = new ProductRequest("physic 2", 14L, 154_000D);

        // when
        productInitializer(request);

        //then
        webclient.get()
                .uri("/api/v1/product/{id}", productResponse.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ProductResponse.class)
                .value(response -> assertThat(response.getName()).isEqualTo("physic 2"));
    }

    @Test
    @WithMockUser(username = "user" , password = "pass",authorities = {"SCOPE_read","SCOPE_internal","SCOPE_write","ROLE_ADMIN"})
    @DisplayName("this integration test for test get product by ids and called from order service then need to have internal scope ")
    void itShouldGetProductByIds() {
        // given

        ProductRequest product1 = new ProductRequest("mathematics 1", 12L, 173_000D);
        ProductRequest product2 = new ProductRequest("chemistry 1", 43L, 211_000D);
        final List<Long> productIds = new ArrayList<>();

        // when
        productInitializer(product1);
        productIds.add(productResponse.getId());

        productInitializer(product2);
        productIds.add(productResponse.getId());

        //then
        webclient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/product/all")
                        .queryParam("id",productIds)
                        .build()
                ).exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(ProductResponse.class)
                .value(productResponses -> {
                    ProductResponse[] responses = productResponses.toArray(ProductResponse[]::new);
                    assertThat(responses).hasSize(2);
                    assertThat(responses[0].getName()).isEqualTo("mathematics 1");
                    assertThat(responses[1].getName()).isEqualTo("chemistry 1");
                });
    }

    @Test
    @WithMockUser(username = "user" , password = "pass", authorities = {"SCOPE_write","SCOPE_internal","ROLE_ADMIN"})
    @DisplayName("this integration test for decrease number of product on stock when orderPlaced api called")
    void itShouldDecreaseProductQuantity() {
        // given
        ProductRequest product1 = new ProductRequest("xiaomi x4", 44L, 13_400_000D);
        DecreaseProductQuantityRequest request ;

        // when
        productInitializer(product1);
        request = new DecreaseProductQuantityRequest(productResponse.getId(),3L);

        //then
        webclient.put()
                .uri("/api/v1/product/decreaseQuantity")
                .body(Flux.just(request),DecreaseProductQuantityRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Boolean.class)
                .value(result -> assertThat(result).isTrue());
    }

    @Test
    @WithMockUser(username = "user" , password = "pass", authorities = {"SCOPE_write","SCOPE_internal","ROLE_ADMIN"})
    @DisplayName("this integration test for after 1 hour specific order dont have any successfull payment then revert product to stock ")
    void itShouldRevertProduct() {
        // given
        ProductRequest product1 = new ProductRequest("xiaomi x4 pro 5g", 13L, 13_900_000D);
        RevertProductRequest request ;

        // when
        productInitializer(product1);
        request = new RevertProductRequest(productResponse.getId(),3L);

        //then
        webclient.put()
                .uri("/api/v1/product/revertProduct")
                .body(Flux.just(request),RevertProductRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Boolean.class)
                .value(result -> assertThat(result).isTrue());
    }
}