package com.carlosvc.repaso_springboot.Albumes.repository;

import com.carlosvc.repaso_springboot.Albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByNombre(String nombre);
    //List<Album> findByNombreAndIsDeletedFalse(String nombre);

    @Query("SELECT al FROM Album al WHERE LOWER(al.discografica.nombre) like %:dicografica% ")
    List<Album> findByDiscograficaContainsIgnoreCase(String discografica);
    //List<Album> findByDiscograficaContainsIgnoreCaseAndIsDeletedFalse(String discografica);

    @Query("SELECT al FROM  Album al WHERE al.nombre =:nombre AND LOWER(al.discografica.nombre) like %:discografica%")
    List<Album> findByNombreAndDiscograficaContainsIgnoreCase(String nombre, String discografica);
    //List<Album> findByNombreAndDiscograficaContainsIgnoreCaseAndIsDeletedFalse(String nombre, String discografica);



    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    List<Album> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Album a SET a.isDeleted =true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
}
