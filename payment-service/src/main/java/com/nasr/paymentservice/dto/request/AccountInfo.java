package com.nasr.paymentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    private Long cardNumber;
    private String cvv2;
    private String expirationTime;

}
