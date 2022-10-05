package com.nasr.productservice.service;

import com.nasr.productservice.base.service.BaseService;
import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.DecreaseProductQuantityRequest;
import com.nasr.productservice.dto.request.RevertProductRequest;
import com.nasr.productservice.dto.response.ProductResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService extends BaseService<Product, ProductResponse,Long> {

    Flux<ProductResponse> getProductByIds(List<Long> ids);


    Mono<Boolean> decreaseQuantity(List<DecreaseProductQuantityRequest> dtos);

    Mono<Boolean> revertProducts(List<RevertProductRequest> revertProductRequests);
}
