package com.nasr.orderservice.exception;

public class OrderDetailNotFoundException extends EntityNotFoundException{

    public OrderDetailNotFoundException(String message) {
        super(message);
    }
}
