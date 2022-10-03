package com.nasr.productservice.base.mapper;

import java.util.List;

public interface BaseMapper <E,D,V>{

    D convertEntityToDto(E entity);
    E convertViewToEntity(V viewDto);

    List<D> convertEntitiesToDtoList(List<E> entities);
    List<E> convertViewsToEntities(List<V> dtoList);
}
