package com.carlosvc.repaso_springboot.rest.Albumes.repository;

import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT al from Album al where al.discografica.usuario.id = :usuarioId")
    Page<Album> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("select al from Album al where al.discografica.usuario.id = :usuarioId")
    List<Album> findByUsuarioId(Long usuarioId);

    @Query("SELECT CASE when count(al) > 0 then true else false END from Album al where al.discografica.usuario.id = :id")
    boolean existsByUsuarioId(Long id);


    @Modifying
    @Query("UPDATE Album a SET a.isDeleted =true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);


    List<Album> findByDiscografica(Discografica discografica);
}