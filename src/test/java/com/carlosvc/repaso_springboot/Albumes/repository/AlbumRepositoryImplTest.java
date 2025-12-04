package com.carlosvc.repaso_springboot.Albumes.repository;

import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class AlbumRepositoryImplTest {

    private final Discografica discografica1 = Discografica.builder().nombre("Black Light").build();
    private final Discografica discografica2 = Discografica.builder().nombre("The Reaper").build();

    private final Album album1 = Album.builder()
            .id(1L)
            .nombre("The End")
            .anio(1993)
            .banda("Ad Hominem")
            .genero("Black Metal")
            .precio(15.90)
            .discografica(discografica1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2L)
            .nombre("Bergtatt")
            .anio(1994)
            .banda("Ulver")
            .genero("Black Metal")
            .precio(14.90)
            .discografica(discografica2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    @Autowired
    private AlbumRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.persist(discografica1);
        entityManager.persist(discografica2);
        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.flush();
    }

    @Test
    void findAll() {
        List<Album> albums = repository.findAll();

        assertAll("findAll",
                () -> assertNotNull(albums),
                () -> assertEquals(2, albums.size())
        );
    }

    @Test
    void findAllByNombre() {
        String nombre = "The End";
        List<Album> albums = repository.findByNombre(nombre);

        assertAll("findAllByNombre",
                () -> assertNotNull(albums),
                () -> assertEquals(1, albums.size()),
                () -> assertEquals(nombre, albums.getFirst().getNombre())
        );
    }

    @Test
    void findAllByDiscografica() {
        String discografica = "Black Light";
        List<Album> albums = repository.findByDiscograficaContainsIgnoreCase(discografica.toLowerCase());

        assertAll("findAllByDiscografica",
                () -> assertNotNull(albums),
                () -> assertEquals(1, albums.size()),
                () -> assertEquals(discografica, albums.getFirst().getDiscografica().getNombre())
        );
    }

    @Test
    void findAllByNombreAndDiscografica() {
        String nombre = "Bergtatt";
        String discografica = "The Reaper";
        List<Album> albums = repository.findByNombreAndDiscograficaContainsIgnoreCase(nombre, discografica);

        assertAll("findAllByNombreAndBanda",
                () -> assertNotNull(albums),
                () -> assertEquals(1, albums.size()),
                () -> assertEquals(nombre, albums.getFirst().getNombre()),
                () -> assertEquals(discografica, albums.getFirst().getDiscografica().getNombre())
        );
    }

    @Test
    void findById_existingID_returnsOptionalWithAlbum() {
        Long id = 1L;
        Optional<Album> optionalAlbum = repository.findById(id);

        assertAll("findById_existingID_returnsOptionalWithAlbum",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(id, optionalAlbum.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        Long id = 5L;
        Optional<Album> optionalAlbum = repository.findById(id);

        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid() {
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Album> optionalAlbum = repository.findByUuid(uuid);

        assertAll("findByUuid_existingUuid",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(uuid, optionalAlbum.get().getUuid())
        );
    }

    @Test
    void findByUuid_NotexistingUuid() {
        UUID uuid = UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee7");
        Optional<Album> optionalAlbum = repository.findByUuid(uuid);

        assertAll("findByUuid_existingUuid",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }

    @Test
    void existsById() {
        Long id = 1L;
        boolean exists = repository.existsById(id);
        assertTrue(exists);
    }

    @Test
    void existsById_NotexistingID() {
        Long id = 5L;
        boolean exists = repository.existsById(id);
        assertFalse(exists);
    }

    @Test
    void existsByUuid() {
        UUID uuid = UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5");
        boolean exists = repository.existsByUuid(uuid);
        assertTrue(exists);
    }

    @Test
    void existsByUuid_NotexistingUuid() {
        UUID uuid = UUID.fromString("67807bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exist = repository.existsByUuid(uuid);
        assertFalse(exist);
    }

    @Test
    void save_notExist() {
        // CORREGIDO: Se añade una discográfica válida para evitar errores de integridad
        Album album = Album.builder()
                .nombre("Altar")
                .anio(1989)
                .banda("Morbid Angel")
                .genero("Death Metal")
                .precio(20.89)
                .discografica(discografica1)
                .build();

        Album savedAlbum = repository.save(album);
        var all = repository.findAll();

        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(album, savedAlbum),
                () -> assertEquals(3, all.size())
        );
    }

    @Test
    void save_ButExist() {
        Long id = 1L;
        Album albumExistente = Album.builder()
                .id(id)
                .nombre("The End")
                .anio(1993)
                .banda("Ad Hominem")
                .genero("Black Metal")
                .precio(15.90)
                .discografica(discografica1) // Importante mantener consistencia
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
                .build();

        Album savedAlbum = repository.save(albumExistente);
        var all = repository.findAll();

        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertTrue(repository.existsById(id)),
                () -> assertTrue(all.size() >= 2)
        );
    }

    @Test
    void deleteById() {
        Long id = 1L;
        repository.deleteById(id);
        var allAlbums = repository.findAll();

        assertAll("deleteById_existing",
                () -> assertEquals(1, allAlbums.size()),
                () -> assertFalse(repository.existsById(id))
        );
    }

    @Test
    void deleteByUuid_existingUuid() {
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        repository.deleteByUuid(uuid);
        var all = repository.findAll();

        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(repository.existsByUuid(uuid))
        );
    }
}