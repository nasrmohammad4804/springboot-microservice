package com.nasr.orderservice.mapper;

import com.nasr.orderservice.base.mapper.BaseMapper;
import com.nasr.orderservice.domain.Order;
import com.nasr.orderservice.dto.request.OrderRequest;
import com.nasr.orderservice.dto.response.OrderResponse;
import org.mapstruct.Mapper;


@Mapper
public interface OrderMapper extends BaseMapper<Order, OrderResponse, OrderRequest> {

}
