package com.nasr.productservice.repository;

import com.nasr.productservice.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataR2dbcTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository underTest;

    @Test
    @DisplayName("this unit test for check product exists in stock by name ")
    void itShouldIsExistsByName() {
        // given
        Product product = new Product("hp", 23L, 29_000_000D);

        // when
        Mono<Product> productMono = underTest.save(product);
        Mono<Boolean> monoResult = underTest.isExistsByName("hp");

        //then
        StepVerifier.create(productMono)
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(monoResult)
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for check dont have any product with product name asus")
    public void itShouldNotExistsByName() {

        Product product = new Product("hp", 23L, 29_000_000D);

        Mono<Product> productMono = underTest.save(product);
        Mono<Boolean> monoResult = underTest.isExistsByName("asus");

        StepVerifier.create(productMono)
                .expectNextCount(1L)
                .verifyComplete();

        StepVerifier.create(monoResult)
                .expectNext(Boolean.FALSE)
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for check product was saved we call successfully fetched")
    void itShouldFindAllById() {
        // given
        Product hp = new Product("hp", 23L, 29_000_000D);
        Product macBook = new Product("mac book", 37L, 64_000_000D);
        // when
        underTest.saveAll(Arrays.asList(hp,macBook)).subscribe();
        //then

        List<Long> ids = Arrays.asList(hp.getId(),macBook.getId());
        StepVerifier.create(underTest.findAllById(ids))
                .expectNextCount(2L)
                .verifyComplete();
    }
}