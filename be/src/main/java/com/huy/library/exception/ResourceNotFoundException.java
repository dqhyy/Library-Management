package com.huy.library.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException{
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        super(HttpStatus.NOT_FOUND, resource + " not found with id: " + id);
    }
}
