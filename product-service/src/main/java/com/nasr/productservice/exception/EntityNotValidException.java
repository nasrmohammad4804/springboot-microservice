package com.nasr.productservice.exception;

public class EntityNotValidException extends RuntimeException {
    public EntityNotValidException(String message) {
        super(message);
    }
}
