package com.carlosvc.repaso_springboot.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNameOrEmailExists extends UserExcepcion {
    public UserNameOrEmailExists(String message) {
        super(message);
    }
}
