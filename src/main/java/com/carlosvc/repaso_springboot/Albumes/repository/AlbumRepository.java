package com.carlosvc.repaso_springboot.Albumes.repository;

import com.carlosvc.repaso_springboot.Albumes.models.Album;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository {
    List<Album> findAll();
    List<Album> findAllByNombre(String nombre);

    List<Album> findAllByBanda(String banda);

    List<Album> findAllByNombreAndBanda(String nombre, String banda);

    Optional<Album> findById(Long id);

    Optional<Album> findByUuid(UUID uuid);

    boolean existsById(Long id);

    boolean existsByUuid(UUID uuid);

    Album save(Album album);

    void deleteById(Long id);

    void deleteByUuid(UUID uuid);

    Long nextId();

}
