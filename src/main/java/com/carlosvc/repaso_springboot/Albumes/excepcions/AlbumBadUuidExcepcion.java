package com.carlosvc.repaso_springboot.Albumes.excepcions;

public class AlbumBadUuidExcepcion extends AlbumExcepcion {
    public AlbumBadUuidExcepcion(String uuid) {
        super("EL UUID: " + uuid + " no es válido");
    }
}
