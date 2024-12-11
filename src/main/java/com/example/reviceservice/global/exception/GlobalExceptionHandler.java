package com.example.reviceservice.global.exception;

import com.example.reviceservice.global.handler.ErrorHandler;
import com.example.reviceservice.global.message.GlobalMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler({ProductException.class,MemberException.class, ReviewException.class})
    public ResponseEntity<ErrorHandler> globalExceptionHandler(Exception ex) {
        ErrorHandler errorHandler = new ErrorHandler(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorHandler, HttpStatus.NOT_FOUND);
    }


}