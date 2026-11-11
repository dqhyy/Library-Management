package com.huy.library.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter 
public class BusinessException extends RuntimeException{
    private final HttpStatus status;

    public BusinessException(String message) {
        this(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }


}
