package com.nasr.orderhandlerservice.model.response;

import com.nasr.orderhandlerservice.model.enumeration.PaymentMode;
import com.nasr.orderhandlerservice.model.enumeration.PaymentStatus;
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
