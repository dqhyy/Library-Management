package com.huy.library.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super(HttpStatus.CONFLICT, resource + " already exists with " + field + ": " + value);
    }
}
    
