package com.nasr.orderservice.domain;

import com.nasr.orderservice.base.domain.BaseEntity;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

import static com.nasr.orderservice.domain.Order.TABLE_NAME;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TABLE_NAME)
@Builder
public class Order extends BaseEntity<Long> {

    public static final String TABLE_NAME = "order_table";
    public static final String ORDER_DATE ="order_date";
    public static final String TOTAL_PRICE ="total_price";
    public static final String ORDER_STATUS ="order_status";

    @Column(value = ORDER_DATE)
    private LocalDateTime orderDate;

    @Column(value = TOTAL_PRICE)
    private Double totalPrice;

    @Column(value = ORDER_STATUS)
    private String orderStatus;


}
