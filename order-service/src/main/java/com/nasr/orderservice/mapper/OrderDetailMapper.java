package com.nasr.orderservice.mapper;

import com.nasr.orderservice.base.mapper.BaseMapper;
import com.nasr.orderservice.domain.OrderDetail;
import com.nasr.orderservice.dto.request.OrderDetailRequest;
import com.nasr.orderservice.dto.response.OrderDetailResponse;
import org.mapstruct.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail, OrderDetailResponse, OrderDetailRequest> {
}
