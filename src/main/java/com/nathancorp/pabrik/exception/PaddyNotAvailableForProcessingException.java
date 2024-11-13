package com.nathancorp.pabrik.exception;

import jakarta.persistence.EntityNotFoundException;

public class PaddyNotAvailableForProcessingException extends EntityNotFoundException {
    public PaddyNotAvailableForProcessingException(String message) {
        super(message);
    }
}
