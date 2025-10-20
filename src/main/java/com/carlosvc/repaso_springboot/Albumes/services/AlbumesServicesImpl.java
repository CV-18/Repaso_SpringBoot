package com.carlosvc.repaso_springboot.Albumes.services;


import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Albumes.mappers.AlbumMapper;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Albumes.repository.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"albums"})
@Slf4j
@Service
public class AlbumesServicesImpl implements AlbumesService{
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Autowired
    public AlbumesServicesImpl(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;

    }


    @Override
    public List<AlbumResponseDto> findAll(String nombre, String banda) {
        // Si todo está vacío o nulo, devolvemos todos los albumes
        if ((nombre == null || nombre.isEmpty()) && (banda == null || banda.isEmpty())) {
            log.info("Buscando todos los albumes");
            return albumMapper.toAlbumResponseDto(albumRepository.findAll());
        }
        // Si el numero no está vacío, pero el titular si, buscamos por numero
        if ((nombre != null && !nombre.isEmpty()) && (banda == null || banda.isEmpty())) {
            log.info("Buscando albumes por nombre: " + nombre);
            return albumMapper.toAlbumResponseDto(albumRepository.findAllByNombre(nombre));
        }
        // Si el numero está vacío, pero el titular no, buscamos por titular
        if (nombre == null || nombre.isEmpty()) {
            log.info("Buscando albumes por banda: " + banda);
            return albumMapper.toAlbumResponseDto(albumRepository.findAllByBanda(banda));
        }
        // Si el numero y el titular no están vacíos, buscamos por ambos
        log.info("Buscando albumes por nombre: " + nombre + " y banda: " + banda);
        return albumMapper.toAlbumResponseDto(albumRepository.findAllByNombreAndBanda(nombre, banda));
    }

    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando alnum por id {}", id);
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundExcepcion(id)));
    }


    @Cacheable(key = "#uuid")
    @Override
    public AlbumResponseDto findbyUuid(String uuid) {
        log.info("Buscando tarjeta por uuid: " + uuid);
        var myUUID = UUID.fromString(uuid);
        return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(myUUID)
                .orElseThrow(() -> new AlbumNotFoundExcepcion(myUUID)));
    }

    @Cacheable(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
        log.info("Guardando tarjeta: " + albumCreateDto);
        // obtenemos el id de tarjeta
        Long id = albumRepository.nextId();

        Album nuevoAlbum = albumMapper.toAlbum(id, albumCreateDto);

        // La guardamos en el repositorio
        return albumMapper.toAlbumResponseDto(albumRepository.save(nuevoAlbum));
    }

    @Cacheable(key = "#result.id")

    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando tarjeta por id: " + id);
        var albumActual = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        // Actualizamos la tarjeta con los datos que nos vienen
        Album albumActualizado =  albumMapper.toAlbum(albumUpdateDto,albumActual);
        // La guardamos en el repositorio
        return albumMapper.toAlbumResponseDto(albumRepository.save(albumActualizado));
    }

    @Cacheable(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando tarjeta por id: " + id);
        var albumEncontrado = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        // La borramos del repositorio si existe
        if (albumEncontrado != null)
            albumRepository.deleteById(id);

    }
}
