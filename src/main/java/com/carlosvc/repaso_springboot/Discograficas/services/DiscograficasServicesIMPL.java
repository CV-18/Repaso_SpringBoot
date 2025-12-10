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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"discograficas"})
public class DiscograficasServicesIMPL implements DiscograficasService {
    private final DiscograficasRepository discograficasRepository;
    @Autowired
    private final DiscograficaMappers discograficaMapper;

    @Override
    public Page<Discografica> findAll(Optional<String> nombre, Optional<Boolean>isDeleted, Pageable pageable){
        log.info("Buscando discograficas por nombre: {}, isDeleted: {}", nombre, isDeleted);
        Specification<Discografica> specNombre = ((root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));

        Specification<Discografica> specIsDeleted = ((root, query, criteriaBuilder) ->
                isDeleted.map(d-> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));


        Specification<Discografica> criterio = Specification.allOf(specNombre, specIsDeleted);
        return discograficasRepository.findAll(criterio, pageable);
    }

    @Override
    public Discografica findByNombre(String nombre){
        log.info("Buscando discograficas por nombre");
        return discograficasRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new DiscograficaNotFoundExcepcion(nombre));
    }


    @Override
    @Cacheable(key = "#id")
    public Discografica findById(Long id){
        log.info("Buscando discograficas por id");
        return discograficasRepository.findById(id)
                .orElseThrow(()-> new DiscograficaNotFoundExcepcion(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Discografica save (DiscograficaRequestDTO discograficaRequestDTO){
        log.info("Guardando discografica: {}", discograficaRequestDTO);
        discograficasRepository.findByNombreEqualsIgnoreCase(discograficaRequestDTO.getNombre()).ifPresent(disco ->{
            throw new DiscograficaConfictExcepcion("Ya existe una discografica con el nombre: " + discograficaRequestDTO.getNombre());
        });
        return discograficasRepository.save(discograficaMapper.toDiscografica(discograficaRequestDTO));
    }

    @Override
    @CachePut(key = "#result.id")
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
    @CacheEvict(key = "#id")
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
