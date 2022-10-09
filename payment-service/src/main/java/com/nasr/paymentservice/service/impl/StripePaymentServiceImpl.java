package com.nasr.paymentservice.service.impl;

import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentServiceImpl implements PaymentService {

    @Override
    public void  doPayment(PaymentRequest paymentRequest) throws Exception{

        // we pay this order with accountInfo and base on payment mode
        // we can use third party payment service for do this
        // if payment is successfully then every thing ok
        //otherwise we throw exception which from third party payment
        // in this section we skip actual payment
    }
}
