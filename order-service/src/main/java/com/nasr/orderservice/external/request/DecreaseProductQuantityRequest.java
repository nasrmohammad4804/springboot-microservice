package com.nasr.orderservice.external.request;

import com.nasr.orderservice.dto.request.OrderPlaceRequest;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class DecreaseProductQuantityRequest extends OrderPlaceRequest {

    public DecreaseProductQuantityRequest(Long productId, Long productNumber) {
        super(productId, productNumber);
    }
}
