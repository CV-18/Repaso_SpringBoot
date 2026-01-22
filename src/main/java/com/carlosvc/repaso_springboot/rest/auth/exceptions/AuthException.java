package com.carlosvc.repaso_springboot.rest.auth.exceptions;

public abstract class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
