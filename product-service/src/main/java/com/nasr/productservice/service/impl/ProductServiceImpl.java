package com.nasr.productservice.service.impl;


import com.nasr.productservice.base.service.impl.BaseServiceImpl;
import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.DecreaseProductQuantityRequest;
import com.nasr.productservice.dto.request.ProductRequest;
import com.nasr.productservice.dto.request.RevertProductRequest;
import com.nasr.productservice.dto.response.ProductResponse;
import com.nasr.productservice.exception.EntityNotFoundException;
import com.nasr.productservice.exception.ProductNotFoundException;
import com.nasr.productservice.exception.ProductNotValidException;
import com.nasr.productservice.mapper.ProductMapper;
import com.nasr.productservice.repository.ProductRepository;
import com.nasr.productservice.service.ProductService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl extends BaseServiceImpl<Product, ProductResponse, Long, ProductRepository, ProductRequest> implements ProductService {


    @Override
    @Transactional
    public Mono<ProductResponse> saveOrUpdate(Product entity) {
        Mono<Boolean> existsByName = repository.isExistsByName(entity.getName());

        return existsByName.doOnNext(result -> {
                    if (result)
                        throw new ProductNotValidException("already exists product with name : " + entity.getName());
                })
                .flatMap(result -> super.saveOrUpdate(entity))
                .log();
    }

    public ProductServiceImpl(ProductRepository repository, ProductMapper productMapper) {
        super(repository, productMapper);
    }

    @Override
    public Class<Product> getEntityClass() {
        return Product.class;
    }

    @Override
    public Flux<ProductResponse> getProductByIds(List<Long> ids) {
        return repository.findAllById(ids)
                .collectList()
                .flatMapMany(entities -> Flux.fromIterable(mapper.convertEntitiesToDtoList(entities)))
                .onErrorMap(e -> new EntityNotFoundException(e.getMessage()))
                .log();
    }

    @Override
    @Transactional
    public Mono<Boolean> decreaseQuantity(List<DecreaseProductQuantityRequest> dtos) {
        List<Long> ids = dtos.stream()
                .map(DecreaseProductQuantityRequest::getProductId).toList();

        Flux<Product> products = repository.findAllById(ids);

        return Flux.fromIterable(dtos)
                .flatMap(dto -> products.filter(product -> product.getId().equals(dto.getProductId()))
                        .switchIfEmpty(Mono.error(new ProductNotFoundException("dont find product with id : " + dto.getProductId())))
                        .next()
                        .flatMap(product -> {
                            if (product.getQuantity() < dto.getProductNumber())
                                return Mono.error(new ProductNotValidException("product quantity is sufficient"));

                            product.setQuantity(product.getQuantity() - dto.getProductNumber());
                            return repository.save(product);
                        }))
                .then(Mono.just(Boolean.TRUE));
    }

    @Override
    public Mono<Boolean> revertProducts(List<RevertProductRequest> revertProductRequests) {

        revertProductRequests = revertProductRequests.stream()
                .sorted(Comparator.comparing(RevertProductRequest::getProductId))
                .collect(Collectors.toList());

        List<Long> revertProductIds = revertProductRequests.stream()
                .map(RevertProductRequest::getProductId).toList();

        return repository.findAllById(revertProductIds)
                .onErrorMap(e -> new ProductNotFoundException("you have invalid product id !!! "))
                .zipWithIterable(revertProductRequests)
                .flatMap(tuples2 -> {
                    Product product = tuples2.getT1();
                    RevertProductRequest revertProduct = tuples2.getT2();
                    product.setQuantity(product.getQuantity() + revertProduct.getProductNumber());
                    return repository.save(product);
                })
                .collectList()
                .map(products -> Boolean.TRUE)
                .log();
    }
}
