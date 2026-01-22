package com.carlosvc.repaso_springboot.rest.Albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Album a crear")
public class AlbumCreateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "El año no puede ser nulo")
    @Min(value = 1900, message = "El año no puede ser anterior a 1900")
    @Max(value = 2100, message = "El año no puede ser posterior a 2100")
    private Integer anio;

    @NotBlank(message = "La banda no puede estar vacía")
    @Size(max = 50, message = "El nombre de la banda no puede exceder los 100 caracteres")
    private String banda;

    @NotBlank(message = "El género no puede estar vacío")
    @Size(max = 50, message = "El género no puede exceder los 50 caracteres")
    private String genero;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
    @DecimalMax(value = "9999.99", message = "El precio no puede exceder 9999.99")
    private Double precio;

    @NotBlank
    @Size(max = 50, message = "La discografica no puede exceder los 50 caracteres")
    private String discografica;

}
