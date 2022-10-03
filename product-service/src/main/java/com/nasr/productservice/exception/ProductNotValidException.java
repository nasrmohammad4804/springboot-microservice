package com.nasr.productservice.exception;

public class ProductNotValidException extends EntityNotValidException{
    public ProductNotValidException(String message) {
        super(message);
    }
}
