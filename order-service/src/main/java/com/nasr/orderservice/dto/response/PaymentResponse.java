package com.nasr.orderservice.dto.response;

import com.nasr.orderservice.domain.enumeration.PaymentMode;
import com.nasr.orderservice.domain.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private PaymentMode mode;
    private PaymentStatus status;
    //  .. etc contain card number - cvv2 - ...
}
