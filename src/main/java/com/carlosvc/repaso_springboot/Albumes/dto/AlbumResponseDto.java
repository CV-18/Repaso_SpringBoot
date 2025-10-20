package com.carlosvc.repaso_springboot.Albumes.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AlbumResponseDto {
    private final Long id;
    private final String nombre;
    private final Integer anio;
    private final String banda;
    private final String genero;
    private final Double precio;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID uuid;
}
