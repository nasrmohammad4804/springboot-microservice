package com.nasr.orderservice.exception;

public class OrderNotValidException extends EntityNotValidException{
    public OrderNotValidException(String message) {
        super(message);
    }
}
