package com.carlosvc.repaso_springboot.users.exceptions;

public class UserExcepcion extends RuntimeException {
    public UserExcepcion(String message) {
        super(message);
    }
}
