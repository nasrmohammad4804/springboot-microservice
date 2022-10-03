package com.nasr.productservice.mapper;

import com.nasr.productservice.base.mapper.BaseMapper;
import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.ProductRequestDto;
import com.nasr.productservice.dto.response.ProductResponseDto;
import org.mapstruct.Mapper;


@Mapper
public interface ProductMapper extends BaseMapper<Product, ProductResponseDto, ProductRequestDto> {

}
