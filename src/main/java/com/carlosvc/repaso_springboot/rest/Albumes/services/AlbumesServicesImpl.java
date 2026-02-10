package com.carlosvc.repaso_springboot.rest.Albumes.services;


import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.excepcions.AlbumBadRequestExcepcion;
import com.carlosvc.repaso_springboot.rest.Albumes.excepcions.AlbumBadUuidExcepcion;
import com.carlosvc.repaso_springboot.rest.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.rest.Albumes.mappers.AlbumMapper;
import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.rest.Discograficas.repositories.DiscograficasRepository;
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
import org.springframework.cache.annotation.CachePut;
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
    private final DiscograficasRepository discograficasRepository;

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

    @Override
    public Page<AlbumResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Obteniendo albumes del usuario: {}",usuarioId);
        return albumRepository.findByUsuarioId(usuarioId, pageable)
                .map(albumMapper::toAlbumResponseDto);

    }

    @Override
    public AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum) {
        log.info("Obteniendo albumes del usuario con id: {}", usuarioId);
        var albumes = albumRepository.findByUsuarioId(usuarioId);
        var albumEncontrado = albumes.stream().filter(a -> a.getId().equals(idAlbum))
                .findFirst().orElse(null);
        if (albumEncontrado == null){
            throw new AlbumBadRequestExcepcion("La album " + idAlbum + " no corresponde a este usuario");
        }
        return albumMapper.toAlbumResponseDto(albumEncontrado);
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

    public Discografica checkDiscografica(String nombreDiscografica){
        log.info("Buscando discografica por nombre: " + nombreDiscografica);
        var discografica = discograficasRepository.findByNombreEqualsIgnoreCase(nombreDiscografica);
        if (discografica.isEmpty() || discografica.get().getIsDeleted()){
            throw new AlbumBadRequestExcepcion("La discografica " + nombreDiscografica + " no existe");

        }
        return discografica.get();
    }

    @Cacheable(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
        log.info("Guardando tarjeta: " + albumCreateDto);
        Discografica discografica = checkDiscografica(albumCreateDto.getDiscografica());
        Album albumsaved = albumRepository.save(albumMapper.toAlbum(albumCreateDto, discografica));
        onChange(Notificacion.Tipo.CREATED, albumsaved);

        return albumMapper.toAlbumResponseDto(albumsaved);
    }

    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId) {
        log.info("Guardando album: {} de usuario: {}",albumCreateDto,usuarioId);
        Discografica discografica = checkDiscografica(albumCreateDto.getDiscografica());
        var usuario = discografica.getUsuario();
        if((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new AlbumBadRequestExcepcion("El usuario no se corresponde con el titular");
        }
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

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto, Long usuarioId) {
        log.info("Actualizando tarjeta por id: {}", id);

        var albumActual = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        var usuario = albumActual.getDiscografica().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new AlbumBadRequestExcepcion("El album " + albumUpdateDto.getNombre() + " no corresponde a este usuario");

        }
        Album albumUpdate = albumRepository.save(
                albumMapper.toAlbum(albumUpdateDto, albumActual));
        onChange(Notificacion.Tipo.UPDATE,  albumUpdate);
        return albumMapper.toAlbumResponseDto(
                albumUpdate

        );
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando tarjeta por id: " + id);
        Album albumDeleted = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE,  albumDeleted);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId) {
        log.debug("Borrando tarjeta por id: " + id);
        Album albumDeleted = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundExcepcion(id));
        var usuario = albumDeleted.getDiscografica().getUsuario();
        if((usuario != null) && (!usuario.getId().equals(usuarioId))){
            throw new AlbumBadRequestExcepcion("El album " + id + " no corresponde a este usuario");
        }
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

    @Override
    public List<Album> buscarPorUsuarioId(Long usuarioId) {
        return albumRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Album> buscarPorId(Long id) {
        return albumRepository.findById(id);
    }
}
