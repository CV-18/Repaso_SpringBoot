package com.carlosvc.repaso_springboot.Discograficas.services;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface DiscograficasService {
    Page<Discografica> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Discografica findByNombre(String nombre);

    Discografica findById(Long id);

    Discografica save (DiscograficaRequestDTO discograficaRequestDTO);

    Discografica update (Long id, DiscograficaRequestDTO discograficaRequestDTO);

    void deleteById(Long id);
}
