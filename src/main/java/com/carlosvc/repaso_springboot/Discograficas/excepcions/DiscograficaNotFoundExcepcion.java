package com.carlosvc.repaso_springboot.Discograficas.excepcions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DiscograficaNotFoundExcepcion extends RuntimeException {
    public DiscograficaNotFoundExcepcion(Long id) {
        super("Discografica con id: "  + id + " no ha sido encontrada");
    }

    public DiscograficaNotFoundExcepcion(String discografica) {
        super("Discografica con nombre: "  + discografica + " no encontrado");
    }
}
