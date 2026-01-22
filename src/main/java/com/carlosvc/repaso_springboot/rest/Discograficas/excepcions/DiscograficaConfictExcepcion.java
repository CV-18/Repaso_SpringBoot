package com.carlosvc.repaso_springboot.rest.Discograficas.excepcions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DiscograficaConfictExcepcion extends RuntimeException {
    public DiscograficaConfictExcepcion(String message) {
        super(message);
    }
}
