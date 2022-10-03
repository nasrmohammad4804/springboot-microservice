package com.nasr.productservice.base.service.impl;

import com.nasr.productservice.base.domain.BaseEntity;
import com.nasr.productservice.base.mapper.BaseMapper;
import com.nasr.productservice.base.service.BaseService;
import com.nasr.productservice.domain.Product;
import com.nasr.productservice.exception.EntityNotFoundException;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImpl<E extends BaseEntity<ID>,D,ID extends Serializable,R extends ReactiveCrudRepository<E,ID>,V> implements BaseService<E,D,ID> {

    protected final R repository;
    protected final BaseMapper<E,D,V> mapper;


    public abstract Class<Product> getEntityClass();

    public BaseServiceImpl(R repository, BaseMapper<E, D, V> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<D> saveOrUpdate(E entity) {
        return repository.save(entity)
                .map(mapper::convertEntityToDto)
                .log();
    }

    @Override
    public Mono<D> getById(ID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(() -> new EntityNotFoundException("dont find any "+getEntityClass().getSimpleName()+" with id : "+id)))
                .map(mapper::convertEntityToDto)
                .log();
    }

    @Override
    public Flux<D> getAll() {
        return repository.findAll()
                .collectList()
                .flatMapMany(entities -> {
                    List<D> dtoList = mapper.convertEntitiesToDtoList(entities);
                    return Flux.fromIterable(dtoList);
                });
    }

    @Override
    public Mono<Void> deleteById(ID id) {
        return repository.deleteById(id)
                .log();
    }

    @Override
    public Flux<D> saveAll(Iterable<E> entities) {
        return repository.saveAll(entities)
                .collectList()
                .flatMapMany(entityList -> {
                    List<D> dtoList = mapper.convertEntitiesToDtoList(entityList);
                    return Flux.fromIterable(dtoList);
                });
    }
}
