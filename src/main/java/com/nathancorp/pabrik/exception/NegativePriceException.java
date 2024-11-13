package com.nathancorp.pabrik.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class NegativePriceException extends DataIntegrityViolationException {
    public NegativePriceException(String message) {
        super(message);
    }
}
