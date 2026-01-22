package com.carlosvc.repaso_springboot.rest.Albumes.excepcions;

public class AlbumExcepcion extends RuntimeException {
    public AlbumExcepcion(String message) {
        super(message);
    }
}
