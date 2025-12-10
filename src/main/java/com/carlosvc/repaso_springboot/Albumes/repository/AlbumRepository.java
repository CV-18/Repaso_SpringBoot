package com.carlosvc.repaso_springboot.Albumes.repository;

import com.carlosvc.repaso_springboot.Albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {

    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    List<Album> findByIsDeleted(Boolean isDeleted);


    List<Album> findByBandaContainsIgnoreCase(String banda);
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted =true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
}