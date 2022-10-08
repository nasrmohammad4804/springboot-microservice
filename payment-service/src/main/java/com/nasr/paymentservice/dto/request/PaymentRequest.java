package com.nasr.paymentservice.dto.request;

import com.nasr.paymentservice.domain.enumeration.PaymentMode;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    private PaymentMode mode;

    private AccountInfo accountInfo;

    private Double totalAmount;

    @NotNull
    @Min(value = 1)
    private Long orderId;
}
