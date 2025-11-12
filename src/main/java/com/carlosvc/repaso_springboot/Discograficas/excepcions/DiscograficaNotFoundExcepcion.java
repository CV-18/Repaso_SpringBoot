package com.carlosvc.repaso_springboot.Discograficas.excepcions;

public class DiscograficaNotFoundExcepcion extends RuntimeException {
    public DiscograficaNotFoundExcepcion(Long id) {
        super("Discografica con el id: "  + id + " no encontrado");
    }

    public DiscograficaNotFoundExcepcion(String discografica) {
        super("Discografica con nombre: "  + discografica + " no encontrado");
    }
}
