package com.carlosvc.repaso_springboot.Albumes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AlbumCreateDto {
    @NotBlank(message = "No puede estar el nombre vacío")
    private final String nombre;

    @NotNull(message = "El año no puede ser nulo")
    private final Integer anio;

    @NotBlank(message = "La banda no puede estar vacía")
    private final String banda;

    @NotBlank(message = "El género no puede estar vacío")
    private final String genero;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser positivo")
    private final Double precio;

}
