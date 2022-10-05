package com.nasr.paymentservice.domain;

import com.nasr.paymentservice.base.domain.BaseEntity;
import com.nasr.paymentservice.domain.enumeration.PaymentMode;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import static com.nasr.paymentservice.domain.Payment.TABLE_NAME;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(TABLE_NAME)
@Builder
public class Payment extends BaseEntity<Long> {

    public static final String ORDER_ID = "order_id";
    public static final String TABLE_NAME = "payment_table";
    public static final String PAYMENT_MODE = "payment_mode";
    public static final String PAYMENT_STATUS = "payment_status";

    @Column(value = PAYMENT_MODE)
    private PaymentMode mode;

    @Column(value = PAYMENT_STATUS)
    private PaymentStatus status;

    @Column(value = ORDER_ID)
    private Long orderId;
}
