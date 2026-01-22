package com.carlosvc.repaso_springboot.rest.Albumes.models;

import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ALBUMES")
@Schema(name = "Albumes")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador Album", example = "1")
    private Long id;

    @Column(nullable = false, length = 50)
    @Schema(description = "Nombre del ALbum", example = "Born for Burning")
    private String nombre;

    @Column(nullable = false, length = 4)
    @Schema(description = "Año de lanzamiento del ALbum", example = "1990")
    private Integer anio;

    @Column(nullable = false, length = 20)
    @Schema(description = "Nombre de la Banda", example = "Horna")
    private String banda;

    @Column(nullable = false, length = 20)
    @Schema(description = "NOmbre del genero", example = "Black Metal")
    private String genero;

    @Column(nullable = false)
    @Schema(description = "Valor del ALbum", example = "30.0")
    private Double precio;

    @Builder.Default
    @Column(updatable = false, nullable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de creación del album", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(updatable = false, nullable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de creación del album", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(unique = true, updatable =false, nullable = false)
    @Builder.Default
    @Schema(description = "UUID de la tarjeta", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid = UUID.randomUUID();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    @Schema(description = "Si la tarjeta está eliminada", example = "false")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "discografica_id")
    @Schema(description = "Discografica del album", example = "Hells Headbangers")
    private Discografica discografica;
}
