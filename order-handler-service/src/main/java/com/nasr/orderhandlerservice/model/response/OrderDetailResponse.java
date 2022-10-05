package com.nasr.orderhandlerservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    private Long productId;
    private Long orderId;
    private Long productNumber;
}
