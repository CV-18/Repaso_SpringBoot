package com.carlosvc.repaso_springboot.Albumes.excepcions;

import java.util.UUID;

public class AlbumNotFoundExcepcion extends AlbumExcepcion {
    public AlbumNotFoundExcepcion(Long id) {
        super("Album con el id: " + id + " no ha sido encontrado");
    }
    public AlbumNotFoundExcepcion(UUID uuid) {
        super("El album con el UUID: " + uuid + " no ha sido encontrado");
    }
    public AlbumNotFoundExcepcion(String genero){super("El album con el genero: " + genero + " no existe"); }
}
