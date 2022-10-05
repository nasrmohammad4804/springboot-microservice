package com.nasr.productservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "productName is mandatory")
    private String name;

    @NotNull(message = "quantity of product is mandatory")
    @Min(value = 1)
    private Long quantity;

    @NotNull(message = "product price is mandatory")
    private Double price;
}
