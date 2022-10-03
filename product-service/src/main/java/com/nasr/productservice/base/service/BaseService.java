package com.nasr.productservice.base.service;

import com.nasr.productservice.base.domain.BaseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface BaseService< E extends BaseEntity<ID>,D,ID extends Serializable> {

    Mono<D> saveOrUpdate(E entity);

    Mono<D> getById(ID id);

    Flux<D> getAll();

    Mono<Void> deleteById(ID id);

    Flux<D> saveAll(Iterable<E> entities);
}
