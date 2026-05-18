package com.dipanshushukla.cop_map_auth_service.exception;

public class BadgeIdAlreadyExistsException extends RuntimeException {
    public BadgeIdAlreadyExistsException(String message) {
        super(message);
    }
}
