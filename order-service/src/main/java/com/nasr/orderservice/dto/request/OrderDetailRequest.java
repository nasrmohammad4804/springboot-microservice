package com.nasr.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequest {

    private Long productId;
    private Long orderId;
    private Long productNumber;
}
