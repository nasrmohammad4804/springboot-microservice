package com.nasr.productservice.controller;

import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.ProductRequestDto;
import com.nasr.productservice.dto.response.ProductResponseDto;
import com.nasr.productservice.mapper.ProductMapper;
import com.nasr.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<ProductResponseDto>> addProduct(@RequestBody @Valid ProductRequestDto dto) {
        Product product = productMapper.convertViewToEntity(dto);
        return productService.saveOrUpdate(product)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductResponseDto> getProducts() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponseDto>> getProduct(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .log();
    }
}
