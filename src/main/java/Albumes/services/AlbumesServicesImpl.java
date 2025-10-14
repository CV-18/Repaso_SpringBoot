package Albumes.services;


import Albumes.models.Album;
import Albumes.repository.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AlbumesServicesImpl implements AlbumesService{
    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumesServicesImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }


    @Override
    public List<Album> findAll(String nombre, String banda) {
        // Si todo está vacío o nulo, devolvemos todos los albumes
        if ((nombre == null || nombre.isEmpty()) && (banda == null || banda.isEmpty())) {
            log.info("Buscando todos los albumes");
            return albumRepository.findAll();
        }
        // Si el numero no está vacío, pero el titular si, buscamos por numero
        if ((nombre != null && !nombre.isEmpty()) && (banda == null || banda.isEmpty())) {
            log.info("Buscando albumes por nombre: " + nombre);
            return albumRepository.findAllByNombre(nombre);
        }
        // Si el numero está vacío, pero el titular no, buscamos por titular
        if (nombre == null || nombre.isEmpty()) {
            log.info("Buscando albumes por banda: " + banda);
            return albumRepository.findAllByBanda(banda);
        }
        // Si el numero y el titular no están vacíos, buscamos por ambos
        log.info("Buscando albumes por nombre: " + nombre + " y banda: " + banda);
        return albumRepository.findAllByNombreAndBanda(nombre, banda);
    }

    @Override
    public Album findById(Long id) {
        log.info("Buscando alnum por id {}", id);
        return albumRepository.findById(id).orElse(null);
    }

    @Override
    public Album findbyUuid(String uuid) {
        log.info("Buscando tarjeta por uuid: " + uuid);
        var myUUID = UUID.fromString(uuid);
        return albumRepository.findByUuid(myUUID).orElse(null);
    }

    @Override
    public Album save(Album album) {
        log.info("Guardando tarjeta: " + album);
        // obtenemos el id de tarjeta
        Long id = albumRepository.nextId();
        // Creamos la tarjeta nueva con los datos que nos vienen
        Album nuevoAlbum = new Album(
                id,
                album.getNombre(),
                album.getAnio(),
                album.getBanda(),
                album.getPrecio(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );

        // La guardamos en el repositorio
        return albumRepository.save(nuevoAlbum);
    }

    @Override
    public Album update(Long id, Album album) {
        log.info("Actualizando tarjeta por id: " + id);
        var albumActual = this.findById(id);
        // Actualizamos la tarjeta con los datos que nos vienen
        Album albumActualizado =  new Album(
                albumActual.getId(),
                album.getNombre() != null ? album.getNombre() : albumActual.getNombre(),
                album.getAnio() != null ? album.getAnio() : albumActual.getAnio(),
                album.getBanda() != null ? album.getBanda() : albumActual.getBanda(),
                album.getPrecio() != null ? album.getPrecio() : albumActual.getPrecio(),
                albumActual.getCreatedAt(),
                LocalDateTime.now(),
                albumActual.getUuid()
        );
        // La guardamos en el repositorio
        return albumRepository.save(albumActualizado);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Borrando tarjeta por id: " + id);
        var albumEncontrado = this.findById(id);
        // La borramos del repositorio si existe
        if (albumEncontrado != null)
            albumRepository.deleteById(id);

    }
}
