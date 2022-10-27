package com.nasr.productservice.dto.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RevertProductRequest extends DecreaseProductQuantityRequest{

    public RevertProductRequest( Long productId,  Long productNumber) {
        super(productId, productNumber);
    }
}
