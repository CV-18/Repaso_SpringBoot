package Albumes.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Album {
    private final Long id;
    private final String nombre;
    private final Integer anio;
    private final String banda;
    private final Double precio;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID uuid;
}
