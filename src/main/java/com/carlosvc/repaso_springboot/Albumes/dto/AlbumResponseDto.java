package com.carlosvc.repaso_springboot.Albumes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumResponseDto {
    private  Long id;
    private  String nombre;
    private  Integer anio;
    private  String banda;
    private  String genero;
    private  Double precio;

    private  LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private  UUID uuid;
}
