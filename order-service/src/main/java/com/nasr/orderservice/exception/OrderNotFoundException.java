package com.nasr.orderservice.exception;

public class OrderNotFoundException extends EntityNotFoundException{

    public OrderNotFoundException(String message) {
        super(message);
    }
}
