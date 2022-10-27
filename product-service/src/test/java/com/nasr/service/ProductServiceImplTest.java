package com.nasr.service;

import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.DecreaseProductQuantityRequest;
import com.nasr.productservice.dto.request.RevertProductRequest;
import com.nasr.productservice.dto.response.ProductResponse;
import com.nasr.productservice.exception.ProductNotFoundException;
import com.nasr.productservice.exception.ProductNotValidException;
import com.nasr.productservice.mapper.ProductMapper;
import com.nasr.productservice.repository.ProductRepository;
import com.nasr.productservice.service.ProductService;
import com.nasr.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @Captor
    private ArgumentCaptor<Product> productCapture;

    private ProductService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProductServiceImpl(repository, mapper);
    }

    @Test
    @DisplayName("this unit test for testing save product ")
    void itShouldSaveOrUpdate() {

        // given
        Product hp = new Product("hp", 34L, 32_000_00D);
        Product sameProduct = new Product("hp", 11L, 47_000_000D);

        // when

        given(mapper.convertEntityToDto(any())).willReturn(new ProductResponse());

        given(repository.save(hp)).willReturn(Mono.just(hp));
        Mockito.lenient().when(repository.save(sameProduct)).thenReturn(Mono.just(sameProduct));

        given(repository.isExistsByName(hp.getName())).willReturn(Mono.just(false));
        Mono<ProductResponse> product1 = underTest.saveOrUpdate(hp);

        given(repository.isExistsByName(sameProduct.getName())).willReturn(Mono.just(true));
        Mono<ProductResponse> product2 = underTest.saveOrUpdate(sameProduct);

        //then
        StepVerifier.create(product1.mergeWith(product2))
                .expectNextCount(1)
                .expectError(ProductNotValidException.class)
                .verify();

    }

    @Test
    @DisplayName("this unit test for check get product by id method")
    void itShouldGetProductByIds() {
        // given
        List<Long> ids = List.of(1L, 2L);

        // when
        given(mapper.convertEntitiesToDtoList(any())).willReturn(List.of(
                new ProductResponse(14L, "samsung galaxy note 7", 25L, 17_000_000D, null),
                new ProductResponse(17L, "spring here book", 39L, 423_000D, null)
        ));

        given(repository.findAllById(ids)).willReturn(Flux.fromIterable(
                List.of(
                        new Product("samsung galaxy note 7", 25L, 17_000_000D),
                        new Product("spring here book", 39L, 423_000D)
                )
        ));

        Flux<ProductResponse> products = underTest.getProductByIds(ids);
        //then

        StepVerifier.create(products)
                .assertNext(product -> {
                    assertThat(product).isNotNull();
                    assertThat(product.getId()).isEqualTo(14);
                })
                .assertNext(product -> {
                    assertThat(product).isNotNull();
                    assertThat(product.getId()).isEqualTo(17);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test method must decrease quantity from stock with valid data ")
    void itShouldDecreaseQuantity() {
        // given
        List<DecreaseProductQuantityRequest> decreaseProductQuantities = Arrays.asList(
                new DecreaseProductQuantityRequest(1L,4L),
                new DecreaseProductQuantityRequest(2L,3L)
        );

        // when

        given(repository.findAllById(anyCollection())).willReturn(Flux.just(
                new Product(1L,"iphone",21L,34_000_000D),
                new Product(2L,"iphone",21L,34_000_000D)
        ));

        given(repository.save(any())).willReturn(Mono.just(new Product()));

        Mono<Boolean> result = underTest.decreaseQuantity(decreaseProductQuantities);
        //then
        StepVerifier.create(result)
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    @DisplayName("this unit test for when at least one product dont exists by id then process failed")
    public void itShouldNotDecreaseQuantitiesWhenProductNotFound(){

            // given
            List<DecreaseProductQuantityRequest> decreaseProductQuantities = Arrays.asList(
                    new DecreaseProductQuantityRequest(1L,4L),
                    new DecreaseProductQuantityRequest(2L,3L)
            );

            // when

            given(repository.findAllById(anyCollection())).willReturn(Flux.just(
                    new Product(1L,"iphone",21L,34_000_000D),
                    new Product(5L,"iphone",21L,34_000_000D)
            ));

            given(repository.save(any())).willReturn(Mono.just(new Product()));

            Mono<Boolean> result = underTest.decreaseQuantity(decreaseProductQuantities);
            //then
            StepVerifier.create(result)
                    .expectError(ProductNotFoundException.class)
                    .verify();
        }

    @Test
    @DisplayName("this unit test for when number of specific product more than number of product exists in stock then dont process")
    public void itShouldNotDecreaseQuantitiesWhenProductQuantityNotValid(){

        // given
        List<DecreaseProductQuantityRequest> decreaseProductQuantities = Arrays.asList(
                new DecreaseProductQuantityRequest(1L,4L),
                new DecreaseProductQuantityRequest(2L,7L)
        );

        // when

        given(repository.findAllById(anyCollection())).willReturn(Flux.just(
                new Product(1L,"iphone",21L,34_000_000D),
                new Product(2L,"iphone",5L,34_000_000D)
        ));

        given(repository.save(any())).willReturn(Mono.just(new Product()));

        Mono<Boolean> result = underTest.decreaseQuantity(decreaseProductQuantities);
        //then
        StepVerifier.create(result)
                .expectError(ProductNotValidException.class)
                .verify();
    }


    @Test
    @DisplayName("this unit test for when order fail and we must revert ordered products to stock")
    void itShouldRevertProducts() {
        // given
        List<RevertProductRequest> revertProducts =Arrays.asList(
                new RevertProductRequest(1L,4L),
                new RevertProductRequest(2L,6L)
        );

        // when
        given(repository.findAllById(anyCollection())).willReturn(Flux.just(
                new Product("iphone",16L,54_000_000D),
                new Product("ddd architecture",32L,215_000D)
        ));

        given(repository.save(any())).willReturn(Mono.empty());

        Mono<Boolean> result = underTest.revertProducts(revertProducts);

        //then
        StepVerifier.create(result)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        then(repository).should(times(2)).save(productCapture.capture());


        long [] newProductsQuantities =  productCapture.getAllValues()
                .stream()
                .map(Product::getQuantity)
                .mapToLong(quantity -> quantity)
                .toArray();

        assertThat(newProductsQuantities)
                .containsExactly(20L,38L);

    }
}
