package com.carlosvc.repaso_springboot.Discograficas.excepcions;

public class DiscograficaExcepcion extends RuntimeException {
    public DiscograficaExcepcion(String message) {
        super(message);
    }
}
