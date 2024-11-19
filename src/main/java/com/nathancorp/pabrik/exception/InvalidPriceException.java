package com.nathancorp.pabrik.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class InvalidPriceException extends DataIntegrityViolationException {
    public InvalidPriceException(String message) {
        super(message);
    }
}
