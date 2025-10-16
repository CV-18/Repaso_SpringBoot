package com.carlosvc.repaso_springboot.Albumes.dto;

import lombok.Data;

@Data
public class AlbumesResponseDto {

    private final Long id;

    private final String nombre;
    private final Integer anio;
    private final String banda;
    private final String categoria;
    private final Double precio;

    private final Long createdAt;
    private final Long updatedAt;
    private final String uuid;
}
