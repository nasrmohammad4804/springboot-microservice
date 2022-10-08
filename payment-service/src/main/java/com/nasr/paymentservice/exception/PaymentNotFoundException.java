package com.nasr.paymentservice.exception;

public class PaymentNotFoundException extends EntityNotFoundException{

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
