package com.carlosvc.repaso_springboot.Albumes.services;


import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumBadUuidExcepcion;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Albumes.mappers.AlbumMapper;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.services.DiscograficasService;
import com.carlosvc.repaso_springboot.config.websockets.WebSocketConfig;
import com.carlosvc.repaso_springboot.config.websockets.WebSocketHandler;
import com.carlosvc.repaso_springboot.websockets.notifications.dto.AlbumNotificationResponse;
import com.carlosvc.repaso_springboot.websockets.notifications.mappers.AlbumNotificationMApper;
import com.carlosvc.repaso_springboot.websockets.notifications.models.Notificacion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = {"albumes"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumesServicesImpl implements AlbumesService,InitializingBean{
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final DiscograficasService discograficasService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlbumNotificationMApper  albumNotificationMApper;
    private WebSocketHandler  webSocketService;

    public void afterPropertiesSet() {
        this.webSocketService = (WebSocketHandler) this.webSocketConfig.webSocketAlbumesHandler();
    }
    public void setWebSocketService(WebSocketHandler webSocketHandler  ) {
        this.webSocketService = webSocketHandler;
    }

    @Override
    public Page<AlbumResponseDto> findAll(Optional<String> nombre, Optional<String> discografica, Optional<Boolean> isDeleted, Pageable pageable) {

        log.info("Buscando albumes por nombre: {}, discografica: {}, isDeleted: {}", nombre, discografica, isDeleted);
        Specification<Album> specNombreAlbum = ((root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));


        Specification<Album>  specDiscografica = (root, query, criteriaBuilder) ->
                discografica.map(d -> {
                    Join<Album, Discografica> discograficaJoin = root.join("discografica");
                    return criteriaBuilder.like(criteriaBuilder.lower(discograficaJoin.get("nombre")), "%" + d.toLowerCase() + "%");

                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));



        Specification<Album> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));



        Specification<Album> criterio = Specification.allOf(specNombreAlbum, specDiscografica,specIsDeleted);


        return albumRepository.findAll(criterio,pageable)
                .map(albumMapper::toAlbumResponseDto);

    }

    @Override
    @Cacheable(key = "#banda")
    public List<AlbumResponseDto> findByBanda(String banda) {
        log.info("Buscando album por banda: {}",banda);
        return albumRepository.findByBandaContainsIgnoreCase(banda).stream()
                .map(albumMapper::toAlbumResponseDto)
                .collect(Collectors.toList());
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

        return albumMapper.toAlbumResponseDto(albumsaved);
    }

    @Cacheable(key = "#result.id")

    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando tarjeta por id: " + id);
        var albumActual = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        Album albumUpdate = albumRepository.save(
                albumMapper.toAlbum(albumUpdateDto, albumActual));
        onChange(Notificacion.Tipo.UPDATE,  albumUpdate);
        return albumMapper.toAlbumResponseDto(albumUpdate);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando tarjeta por id: " + id);
        Album albumDeleted = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE,  albumDeleted);
    }

    void onChange(Notificacion.Tipo tipo, Album data) {
        log.debug("Servicio de productos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = (WebSocketHandler) this.webSocketConfig.webSocketAlbumesHandler();
        }
        try {
            Notificacion<AlbumNotificationResponse>notificacion = new Notificacion<>(
                    "ALBUMES",
                    tipo,
                    albumNotificationMApper.toAlbumNotificationResponse(data),
                    LocalDateTime.now().toString()
            );
            String json =objectMapper.writeValueAsString((notificacion));
            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.setName("WebSocketTarjeta-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }

    }
}
