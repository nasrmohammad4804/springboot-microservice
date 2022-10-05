package com.nasr.productservice.mapper;

import com.nasr.productservice.base.mapper.BaseMapper;
import com.nasr.productservice.domain.Product;
import com.nasr.productservice.dto.request.ProductRequest;
import com.nasr.productservice.dto.response.ProductResponse;
import org.mapstruct.Mapper;


@Mapper
public interface ProductMapper extends BaseMapper<Product, ProductResponse, ProductRequest> {

}
