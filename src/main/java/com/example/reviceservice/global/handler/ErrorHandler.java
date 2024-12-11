package com.example.reviceservice.global.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorHandler {

    private String message;
    private Integer errorCode;
}
