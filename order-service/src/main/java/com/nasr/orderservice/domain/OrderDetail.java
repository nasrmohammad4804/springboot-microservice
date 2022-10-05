package com.nasr.orderservice.domain;

import com.nasr.orderservice.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import static com.nasr.orderservice.domain.OrderDetail.TABLE_NAME;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name =TABLE_NAME )
public class OrderDetail extends BaseEntity<Long> {

    public static final String ORDER_ID="order_id";
    public static final String PRODUCT_ID="product_id";
    public static final String PRODUCT_NUMBER="product_number";
    public static final String TABLE_NAME ="order_detail_table";

    @Column(value = ORDER_ID)
    private Long orderId;

    @Column(value = PRODUCT_ID)
    private Long productId;

    @Column(value = PRODUCT_NUMBER)
    private Long productNumber;
}
