package com.nasr.paymentservice.service;

import com.nasr.paymentservice.dto.request.PaymentRequest;

public interface PaymentService {

    boolean doPayment(PaymentRequest paymentRequest);
}
