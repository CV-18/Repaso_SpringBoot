package com.carlosvc.repaso_springboot.Albumes.dto;

import lombok.Data;

@Data
public class AlbumesCreateDto {
    private final String nombre;
    private Integer anio;
    private final String banda;
    private final String categoria;
    private final Double precio;
}
