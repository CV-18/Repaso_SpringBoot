package com.carlosvc.repaso_springboot.rest.Albumes.excepcions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlbumNotFoundExcepcion extends AlbumExcepcion {
    public AlbumNotFoundExcepcion(Long id) {
        super("Album con el id: " + id + " no encontrado");
    }
    public AlbumNotFoundExcepcion(UUID uuid) {
        super("El album con el UUID: " + uuid + " no encontrado");
    }
    public AlbumNotFoundExcepcion(String genero){super("El album con el genero: " + genero + " no existe"); }
}
