package com.nasr.orderservice.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ExternalServiceException  extends RuntimeException{
    private HttpStatus errorCode;

    public ExternalServiceException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
