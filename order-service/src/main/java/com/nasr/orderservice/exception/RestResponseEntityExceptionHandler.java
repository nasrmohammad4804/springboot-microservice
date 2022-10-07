package com.nasr.orderservice.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotValidException.class)
    public ResponseEntity<ErrorResponse> entityNotValidExceptionHandler(EntityNotValidException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFoundExceptionHandler(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage(),HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> externalServiceException(ExternalServiceException e){
        return ResponseEntity.status(e.getErrorCode())
                .body(new ErrorResponse(e.getMessage(),e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
