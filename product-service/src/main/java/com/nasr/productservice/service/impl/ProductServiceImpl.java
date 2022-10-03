package com.nasr.productservice.service.impl;


import com.nasr.productservice.base.service.impl.BaseServiceImpl;
import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.ProductRequestDto;
import com.nasr.productservice.dto.response.ProductResponseDto;
import com.nasr.productservice.exception.ProductNotValidException;
import com.nasr.productservice.mapper.ProductMapper;
import com.nasr.productservice.repository.ProductRepository;
import com.nasr.productservice.service.ProductService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, ProductResponseDto, Long, ProductRepository, ProductRequestDto> implements ProductService {


    @Override
    public Mono<ProductResponseDto> saveOrUpdate(Product entity) {
        Mono<Boolean> existsByName = repository.isExistsByName(entity.getName());

        return existsByName.doOnNext(result -> {
            if (result)
                throw new ProductNotValidException("already exists product with name : " + entity.getName());
        })
                .flatMap(result -> super.saveOrUpdate(entity) )
                .log();
    }

    public ProductServiceImpl(ProductRepository repository, ProductMapper productMapper) {
        super(repository, productMapper);
    }

    @Override
    public Class<Product> getEntityClass() {
        return Product.class;
    }
}
