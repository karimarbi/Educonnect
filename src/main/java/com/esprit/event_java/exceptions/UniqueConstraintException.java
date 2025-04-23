package com.esprit.event_java.exceptions;

public class UniqueConstraintException extends ValidationException {
    public UniqueConstraintException(String message) {
        super(message);
    }
}