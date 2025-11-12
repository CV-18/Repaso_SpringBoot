package com.carlosvc.repaso_springboot.Albumes.repository;

import com.carlosvc.repaso_springboot.Albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByNombre(String nombre);
    List<Album> findByNombreAndIsDeletedFalse(String nombre);

    List<Album> findByDiscograficaContainsIgnoreCase(String discografica);
    List<Album> findByDiscograficaContainsIgnoreCaseAndIsDeletedFalse(String discografica);

    List<Album> findByNombreAndDiscograficaContainingIgnoreCase(String nombre, String discografica);
    List<Album> findByNombreAndDiscograficaContainingIgnoreCaseAndIsDeletedFalse(String nombre, String discografica);



    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    List<Album> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Album a SET a.isDeleted =true WHERE a.id = :id")

    void updateIsDeletedToTrueById(Long id);
}
