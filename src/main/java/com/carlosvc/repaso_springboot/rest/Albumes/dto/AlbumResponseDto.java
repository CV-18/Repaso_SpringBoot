package com.carlosvc.repaso_springboot.rest.Albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Album a devolver como respuesta")
public class AlbumResponseDto {
    @Schema(description = "Identificador del album", example = "1")
    private  Long id;

    @Schema(description = "Nombre del album", example = "Aske")
    private  String nombre;

    @Schema(description = "Año del album", example = "1993")
    private  Integer anio;

    @Schema(description = "Nombre de la banda", example = "Bathory")
    private  String banda;

    @Schema(description = "Nombre del genero", example = "Black Metal")
    private  String genero;

    @Schema(description = "Precio del album", example = "20.0")
    private  Double precio;

    @Schema(description = "Discografica del album", example = "Black Light")
    private String discografica;

    @Schema(description = "Fecha de creación del album", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualización del album", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime updatedAt;

    @Schema(description = "UUID del album", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid;
}
