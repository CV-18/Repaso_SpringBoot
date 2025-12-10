package com.carlosvc.repaso_springboot.Albumes.excepcions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlbumBadUuidExcepcion extends AlbumExcepcion {
    public AlbumBadUuidExcepcion(String uuid) {
        super("EL UUID: " + uuid + " no es válido");
    }
}
