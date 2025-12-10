package com.carlosvc.repaso_springboot.Discograficas.services;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.excepcions.DiscograficaConfictExcepcion;
import com.carlosvc.repaso_springboot.Discograficas.excepcions.DiscograficaNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Discograficas.mappers.DiscograficaMappers;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.repositories.DiscograficasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"discograficas"})
public class DiscograficasServicesIMPL implements DiscograficasService {
    public final DiscograficasRepository  discograficasRepository;
    public final DiscograficaMappers discograficaMapper;
    private final DiscograficaMappers discograficaMappers;

    @Override
    public Page<Discografica> findAll(Optional<String> nombre, Optional<Boolean>isDeleted, Pageable pageable){
        log.info("Buscando discograficas por nombre: {}, isDeleted: {}", nombre, isDeleted);
        Specification<Discografica> specNombre = ((root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));

        Specification<Discografica> specIsDeleted = ((root, query, criteriaBuilder) ->
                isDeleted.map(d-> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));


        Specification<Discografica>
    }

    @Override
    public Discografica findByNombre(String nombre){
        log.info("Buscando discograficas por nombre");
        return discograficasRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new DiscograficaNotFoundExcepcion(nombre));
    }


    @Override
    public Discografica findById(Long id){
        log.info("Buscando discograficas por id");
        return discograficasRepository.findById(id)
                .orElseThrow(()-> new DiscograficaNotFoundExcepcion(id));
    }

    @Override
    @CachePut
    public Discografica save (DiscograficaRequestDTO discograficaRequestDTO){
        log.info("Guardando discografica: {}", discograficaRequestDTO);
        discograficasRepository.findByNombreEqualsIgnoreCase(discograficaRequestDTO.getNombre()).ifPresent(disco ->{
            throw new DiscograficaConfictExcepcion("Ya existe una discografica con el nombre: " + discograficaRequestDTO.getNombre());
        });
        return discograficasRepository.save(discograficaMappers.toDiscografica(discograficaRequestDTO));
    }

    @Override
    @CachePut
    public Discografica update(Long id, DiscograficaRequestDTO discograficaRequestDTO){
        log.info("Actualizando discografica: {}", discograficaRequestDTO);
        Discografica discograficaActual = findById(id);
        discograficasRepository.findByNombreEqualsIgnoreCase(discograficaRequestDTO.getNombre())
                .ifPresent(disco ->{
                    if (!disco.getId().equals(id)) {
                        throw new DiscograficaConfictExcepcion("Ya existe una discografica con el nombre: " + discograficaRequestDTO.getNombre());
                    }

                });
        return discograficasRepository.save(discograficaMapper.toDiscografica(discograficaRequestDTO, discograficaActual));

    }


    @Override
    @CacheEvict
    @Transactional
    public void deleteById(Long id){
        log.info("Eliminando discografica: {}", id);
        Discografica discografica = findById(id);
        if (discograficasRepository.existsAlbumById(id)){
            String mensaje = "No se puede borrar la discografica con el id: " + id + ", al tener albumes asociados";
            log.warn(mensaje);
            throw new DiscograficaConfictExcepcion(mensaje);
        }else {
            discograficasRepository.deleteById(id);
        }
    }
}
