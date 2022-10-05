package com.nasr.orderservice.base.mapper;

import java.util.List;

public interface BaseMapper <E,D,V>{

    D convertEntityToDto(E entity);
    E convertViewToEntity(V viewDto);

    List<D> convertEntitiesToDtoList(Iterable<E> entities);
    List<E> convertViewsToEntities(Iterable<V> dtoList);
}
