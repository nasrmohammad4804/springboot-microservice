package com.nasr.productservice.domain;

import com.nasr.productservice.base.domain.BaseEntity;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = Product.PRODUCT_TABLE)
public class Product extends BaseEntity<Long> {

    public static final String PRODUCT_TABLE = "product_table";

    private String name;
    private Long quantity;
    private Double price;

    private Long categoryId;

    public Product(String name, Long quantity, Double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
