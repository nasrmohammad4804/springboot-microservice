package com.nasr.orderhandlerservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevertProductRequest {

    private Long productId;
    private Long productNumber;
}
