package com.nasr.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlaceWithPaymentResponse {

    private Long orderId;
    private LocalDateTime orderDate;
    private  List<OrderDetailResponse> orderDetails = new ArrayList<>();

    private PaymentResponse paymentResponse;
}
