package com.carlosvc.repaso_springboot.Albumes.models;

import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
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
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 4)
    private Integer anio;

    @Column(nullable = false, length = 20)
    private String banda;

    @Column(nullable = false, length = 20)
    private String genero;

    @Column(nullable = false)
    private Double precio;

    @Builder.Default
    @Column(updatable = false, nullable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(updatable = false, nullable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(unique = true, updatable =false, nullable = false)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "discografica_id")
    private Discografica discografica;
}
