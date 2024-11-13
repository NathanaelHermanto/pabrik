package com.nathancorp.pabrik.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class NegativeQuantityException extends DataIntegrityViolationException {
    public NegativeQuantityException(String message) {
        super(message);
    }
}
