package com.nasr.orderservice.external.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private Long quantity;
    private Double price;

    public ProductResponse(Long id, Long quantity) {
        this.id = id;
        this.quantity = quantity;
    }
}
