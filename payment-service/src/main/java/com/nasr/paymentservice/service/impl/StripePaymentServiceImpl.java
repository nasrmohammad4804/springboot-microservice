package com.nasr.paymentservice.service.impl;

import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentServiceImpl implements PaymentService {

    @Override
    public boolean doPayment(PaymentRequest paymentRequest) {

        // we pay this order with accountInfo and base on payment mode
        // we can use third party payment service for do this
        // and finally send response of transaction detail
        // in this section we skip actual payment
        return false;
    }
}
