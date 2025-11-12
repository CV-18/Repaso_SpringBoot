package com.carlosvc.repaso_springboot.Discograficas.services;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.List;


public interface DiscograficasService {
    List<Discografica> findAll(String nombre);

    Discografica findByNombre(String nombre);

    Discografica findById(Long id);

    Discografica save (DiscograficaRequestDTO discograficaRequestDTO);

    Discografica update (Long id, DiscograficaRequestDTO discograficaRequestDTO);

    void deleteById(Long id);
}
