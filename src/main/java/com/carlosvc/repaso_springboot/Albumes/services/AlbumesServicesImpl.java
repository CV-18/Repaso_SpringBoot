package com.carlosvc.repaso_springboot.Albumes.services;


import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumBadUuidExcepcion;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Albumes.mappers.AlbumMapper;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.Discograficas.services.DiscograficasService;
import com.carlosvc.repaso_springboot.config.websockets.WebSocketConfig;
import com.carlosvc.repaso_springboot.config.websockets.WebSocketHandler;
import com.carlosvc.repaso_springboot.websockets.notifications.mappers.AlbumNotificationMApper;
import com.carlosvc.repaso_springboot.websockets.notifications.models.Notificacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"albums"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumesServicesImpl implements AlbumesService{
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final DiscograficasService discograficasService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlbumNotificationMApper  albumNotificationMApper;
    private WebSocketHandler  webSocketService;

    public void afterPropertiesSet() {
        this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }
    public void setWebSocketService(WebSocketHandler webSocketHandler  ) {
        this.webSocketService = webSocketHandler;
    }

    @Override
    public List<AlbumResponseDto> findAll(String nombre, String discografica) {

        if ((nombre == null || nombre.isEmpty()) && (discografica == null || discografica.isEmpty())) {
            log.info("Buscando todos los albumes");
            return albumMapper.toAlbumResponseDtoList(albumRepository.findAll());
        }

        if ((nombre != null && !nombre.isEmpty()) && (discografica == null || discografica.isEmpty())) {
            log.info("Buscando albumes por nombre: " + nombre);
            return albumMapper.toAlbumResponseDtoList(albumRepository.findByNombre(nombre));
        }

        if (nombre == null || nombre.isEmpty()) {
            log.info("Buscando albumes por discografica: " + discografica);
            return albumMapper.toAlbumResponseDtoList(albumRepository.findByDiscograficaContainsIgnoreCase(discografica.toLowerCase()));
        }

        log.info("Buscando albumes por nombre: " + nombre + " y discografica: " + discografica);
        return albumMapper.toAlbumResponseDtoList(albumRepository.findByNombreAndDiscograficaContainsIgnoreCase(nombre, discografica.toLowerCase()));
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
        try {
            var myUUID = UUID.fromString(uuid);
            return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlbumNotFoundExcepcion(myUUID)));
        }catch (IllegalArgumentException e){
            throw new AlbumBadUuidExcepcion(uuid);
        }


    }

    @Cacheable(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
        log.info("Guardando tarjeta: " + albumCreateDto);
        var discografica = discograficasService.findByNombre(albumCreateDto.getDiscografica());
        Album albumsaved = albumRepository.save(albumMapper.toAlbum(albumCreateDto, discografica));
        onChange(Notificacion.Tipo.CREATED, albumsaved);

        return albumMapper.toAlbumResponseDto(albumRepository.save(albumsaved));
    }

    @Cacheable(key = "#result.id")

    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando tarjeta por id: " + id);
        var albumActual = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        Album albumActualizado =  albumMapper.toAlbum(albumUpdateDto,albumActual);
        return albumMapper.toAlbumResponseDto(albumRepository.save(albumActualizado));
    }

    @Cacheable(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando tarjeta por id: " + id);
        albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        albumRepository.deleteById(id);

    }
}
