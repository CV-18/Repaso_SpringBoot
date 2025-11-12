package com.carlosvc.repaso_springboot.Albumes.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Album {
    private Long id;

    private String nombre;
    private Integer anio;
    private String banda;
    private String genero;
    private Double precio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}
