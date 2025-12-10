package com.carlosvc.repaso_springboot.Discograficas.repositories;

import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscograficasRepository extends JpaRepository<Discografica, Long>, JpaSpecificationExecutor<Discografica> {
    Optional<Discografica> findByNombreEqualsIgnoreCase(String nombre);

    List<Discografica> findByNombreContainingIgnoreCase(String nombre);

    List<Discografica> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Discografica disco set disco.isDeleted = true  where disco.id = :id")
    void updateIsDeletedToTrueById(Long id);

    @Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM Album al where al.discografica.id = :id")
    boolean existsAlbumById(Long id);

}
