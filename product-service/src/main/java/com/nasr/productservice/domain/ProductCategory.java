package com.nasr.productservice.domain;

import com.nasr.productservice.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = ProductCategory.PRODUCT_CATEGORY_TABLE)
public class ProductCategory extends BaseEntity<Long> {

    public static final String PRODUCT_CATEGORY_TABLE="product_category_table";
    protected String name;
}
