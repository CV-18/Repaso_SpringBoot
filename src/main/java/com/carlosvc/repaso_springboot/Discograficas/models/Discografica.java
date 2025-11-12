package com.carlosvc.repaso_springboot.Discograficas.models;


import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DISCOGRAFICAS")
public class Discografica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false,length = 50)
    private String nombre;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt =  LocalDateTime.now();

    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted = false;


    @OneToMany(mappedBy = "discografica")
    @JsonIgnoreProperties("discografica")
    private List<Album> albumes;

}
