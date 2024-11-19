package com.nathancorp.pabrik.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class InvalidQuantityException extends DataIntegrityViolationException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
