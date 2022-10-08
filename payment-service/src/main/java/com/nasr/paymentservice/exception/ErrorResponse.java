package com.nasr.paymentservice.exception;


import org.springframework.http.HttpStatus;

public record ErrorResponse (String message , HttpStatus errorCode) {
}
