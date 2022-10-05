package com.nasr.orderservice.exception;

public class EntityNotValidException extends RuntimeException{
    public EntityNotValidException(String message) {
        super(message);
    }
}
