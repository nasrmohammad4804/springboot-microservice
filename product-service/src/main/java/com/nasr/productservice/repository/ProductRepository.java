package com.nasr.productservice.repository;

import com.nasr.productservice.domain.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    @Query("select count(p.id)>0 from product_table as p where p.name = :name ")
    Mono<Boolean> isExistsByName(String name);

}
