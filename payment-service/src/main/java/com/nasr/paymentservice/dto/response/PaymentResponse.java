package com.nasr.paymentservice.dto.response;

import com.nasr.paymentservice.domain.enumeration.PaymentMode;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
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
}
