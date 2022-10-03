package com.nasr.productservice.exception;

public class ProductNotFoundException extends EntityNotFoundException{

    public ProductNotFoundException(String message) {
        super(message);
    }
}
