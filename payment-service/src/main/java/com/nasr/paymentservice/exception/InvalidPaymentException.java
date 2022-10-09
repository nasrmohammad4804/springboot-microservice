package com.nasr.paymentservice.exception;

public class InvalidPaymentException extends RuntimeException{
    public InvalidPaymentException(String message) {
        super(message);
    }
}
