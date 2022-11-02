package com.nasr.paymentservice.dto.response;

import com.nasr.paymentservice.domain.enumeration.PaymentMode;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private PaymentMode mode;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}
