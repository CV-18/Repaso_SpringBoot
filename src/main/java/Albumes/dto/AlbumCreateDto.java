package Albumes.dto;

import lombok.Data;

@Data
public class AlbumCreateDto {
    private final String nombre;
    private final Integer anio;
    private final String banda;
    private final String genero;
    private final Double precio;
}
