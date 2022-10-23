package com.nasr.productservice.controller;

import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.DecreaseProductQuantityRequest;
import com.nasr.productservice.dto.request.ProductRequest;
import com.nasr.productservice.dto.request.RevertProductRequest;
import com.nasr.productservice.dto.response.ProductResponse;
import com.nasr.productservice.mapper.ProductMapper;
import com.nasr.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    /**
     * this endpoint access for user have ADMIN or SUPER_ADMIN role because ordinary customer dont allowed to define product in service
     * @param dto as product
     * @return product info
     */
    @PostMapping
    @PreAuthorize(value = "hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Mono<ResponseEntity<ProductResponse>> addProduct(@RequestBody @Valid ProductRequest dto) {
        Product product = productMapper.convertViewToEntity(dto);
        return productService.saveOrUpdate(product)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductResponse> getProducts() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> getProduct(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .log();
    }
    @GetMapping(path = "/all",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_internal') ")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductResponse> getProductByIds(@RequestParam("id") List<Long> ids){
            return productService.getProductByIds(ids);
    }

    @PutMapping(path = "/decreaseQuantity")
    @PreAuthorize("hasAuthority('SCOPE_internal') ")
    public Mono<ResponseEntity<Boolean>> decreaseProductQuantity(@RequestBody List< @Valid DecreaseProductQuantityRequest> dtos){
        return productService.decreaseQuantity(dtos)
                .map(ResponseEntity::ok);
    }
    @PutMapping("/revertProduct")
    @PreAuthorize("hasAuthority('SCOPE_internal')")
    public Mono<ResponseEntity<Boolean>> revertProduct(@RequestBody List< @Valid RevertProductRequest> revertProductRequests){
        return productService.revertProducts(revertProductRequests)
                .map(ResponseEntity::ok);
    }
}
