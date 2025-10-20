package com.carlosvc.repaso_springboot.Albumes.excepcions;

public class AlbumExcepcion extends RuntimeException {
    public AlbumExcepcion(String message) {
        super(message);
    }
}
