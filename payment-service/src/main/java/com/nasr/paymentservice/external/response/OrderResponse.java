package com.nasr.paymentservice.external.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    private LocalDateTime orderDate;

    private Double totalPrice;

    private String orderStatus;
}
