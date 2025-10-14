package Albumes.repository;


import Albumes.models.Album;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class AlbumRepositoryImpl implements AlbumRepository {
    private final Map<Long, Album>albumes = new LinkedHashMap<>(
            Map.of(
                    1l, new Album(1l,
                            "Somberlain",
                            1993,
                            "Dissection",
                            19.90,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            UUID.randomUUID()),

                    2l,new Album(2l,
                            "Draugen",
                            2005,
                            "Burzum",
                            16.90,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            UUID.randomUUID())
            )
    );

    @Override
    public List<Album> findAll() {
        log.debug("Buscando Albumes");
        return albumes.values().stream().toList();
    }

    @Override
    public List<Album> findAllByNombre(String nombre) {
        log.info("Buscando albumes por nombre: " + nombre);
        return albumes.values().stream()
                .filter(album -> album.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public List<Album> findAllByBanda(String banda) {
        log.info("Buscando albumes por banda: " + banda);
        return albumes.values().stream()
                .filter(album -> album.getBanda().toLowerCase().contains(banda.toLowerCase()))
                .toList();
    }

    @Override
    public List<Album> findAllByNombreAndBanda(String nombre, String banda) {
        log.info("Buscando albumes por nombre: {} y banda: {} ", nombre, banda);
        return albumes.values().stream()
                .filter(album -> album.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(album -> album.getBanda().toLowerCase().contains(banda.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Album> findById(Long id) {
        log.info("Buscando albumes por id: " + id);
        return albumes.get(id) != null ? Optional.of(albumes.get(id)) : Optional.empty();
    }

    @Override
    public Optional<Album> findByUuid(UUID uuid) {
        log.info("Buscando albumes por uuid: " + uuid);
        return albumes.values().stream()
                .filter(album -> album.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        log.info("Comprobando si existen albumes por id: " + id);
        return albumes.get(id) != null;
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        log.info("Comprobando si existen albumes por uuid: " + uuid);
        return albumes.values().stream()
                .anyMatch(album -> album.getUuid().equals(uuid));
    }

    @Override
    public Album save(Album album) {
        log.info("Guardando album: " + album);
        albumes.put(album.getId(), album);
        return album;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Borrando album por id: " + id);
        albumes.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        log.info("Borrando album por uuid: " + uuid);
        albumes.values().removeIf(album -> album.getUuid().equals(uuid));
    }

    @Override
    public Long nextId() {
        log.debug("Obteniendo siguiente id del album");
        return albumes.keySet().stream()
                .mapToLong(value -> value)
                .max()
                .orElse(0) + 1;
    }

}
