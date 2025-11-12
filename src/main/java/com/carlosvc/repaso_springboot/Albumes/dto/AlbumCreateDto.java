package com.carlosvc.repaso_springboot.Albumes.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlbumCreateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @NotNull(message = "El año no puede ser nulo")
    @Min(value = 1900, message = "El año no puede ser anterior a 1900")
    @Max(value = 2100, message = "El año no puede ser posterior a 2100")
    private final Integer anio;

    @NotBlank(message = "La banda no puede estar vacía")
    @Size(max = 50, message = "El nombre de la banda no puede exceder los 100 caracteres")
    private final String banda;

    @NotBlank(message = "El género no puede estar vacío")
    @Size(max = 50, message = "El género no puede exceder los 50 caracteres")
    private final String genero;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
    @DecimalMax(value = "9999.99", message = "El precio no puede exceder 9999.99")
    private final Double precio;

    @NotBlank
    @Size(max = 50, message = "La discografica no puede exceder los 50 caracteres")
    private final String discografica;

}
