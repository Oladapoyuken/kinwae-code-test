package com.example.kinwaeassessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(ApiCustomException.class)
    public ResponseEntity<Object> handleGenericApiException(ApiCustomException ace){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ApiException(
                "error",
                ace.getMessage(),
                status,
                ZonedDateTime.now()), status);
    }
}
