package com.nasr.orderservice.base.service;

import com.nasr.orderservice.base.domain.BaseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface BaseService<ID extends Serializable,D,V> {

    Mono<D> saveOrUpdate(V view);

    Mono<D> getById(ID id);

    Flux<D> getAll();

    Mono<Void> deleteById(ID id);

    Flux<D> saveAll(Iterable<V> view);
}
