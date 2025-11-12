package com.carlosvc.repaso_springboot.Albumes.repository;

import com.carlosvc.repaso_springboot.Albumes.models.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumRepositoryImplTest {

    private final Album album1 = Album.builder()
            .id(1l)
            .nombre("The End")
            .anio(1993)
            .banda("Ad Hominem")
            .genero("Black Metal")
            .precio(15.90)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
            .build();

    private final Album album2 = Album.builder()
            .id(2l)
            .nombre("Bergtatt")
            .anio(1994)
            .banda("Ulver")
            .genero("Black Metal")
            .precio(14.90)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();


    @BeforeEach
    void setUp() {
        repository = new AlbumRepositoryImpl();
        repository.save(album1);
        repository.save(album2);
    }

    private AlbumRepositoryImpl repository;
    @Test
    void findAll() {
        List<Album> albums = repository.findAll();

        assertAll("findAll",
                ()-> assertNotNull(albums),
                ()-> assertEquals(2, albums.size())
        );
    }

    @Test
    void findAllByNombre() {
        String nombre = "The End";
        List<Album> albums = repository.findAllByNombre(nombre);

        assertAll("findAllByNombre",
                () -> assertNotNull(albums),
                ()-> assertEquals(1, albums.size()),
                () -> assertEquals(nombre, albums.getFirst().getNombre())
        );
    }

    @Test
    void findAllByBanda() {
        String banda = "Ad Hominem";
        List<Album> albums = repository.findAllByBanda(banda);

        assertAll("findAllByBanda",
                () -> assertNotNull(albums),
                () -> assertEquals(1, albums.size()),
                () -> assertEquals(banda, albums.getFirst().getBanda())
        );

    }

    @Test
    void findAllByNombreAndBanda() {
        String nombre = "Bergtatt";
        String banda = "Ulver";
        List<Album> albums = repository.findAllByNombreAndBanda(nombre, banda);

        assertAll("findAllByNombreAndBanda",
                () -> assertNotNull(albums),
                () -> assertEquals(1,albums.size()),
                () -> assertEquals(nombre, albums.getFirst().getNombre()),
                () -> assertEquals(banda, albums.getFirst().getBanda())
        );
    }

    @Test
    void findById_existingID() {
        Long id = 2l;
        Optional<Album> optionalAlbum = repository.findById(id);

        assertAll("findById_existingID",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(id, optionalAlbum.get().getId())
        );

    }

    @Test
    void findById_NotexistingID() {
        Long id = 5l;
        Optional<Album> optionalAlbum = repository.findById(id);

        assertAll("findById_existingID",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );

    }

    @Test
    void findByUuid_existingUuid() {
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Album> optionalAlbum = repository.findByUuid(uuid);

        assertAll("findByUuid_existingUuid",
                () ->assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () ->assertEquals(uuid, optionalAlbum.get().getUuid())
        );
    }

    @Test
    void findByUuid_NotexistingUuid() {
        UUID uuid = UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee7");
        Optional<Album> optionalAlbum = repository.findByUuid(uuid);

        assertAll("findByUuid_existingUuid",
                () ->assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }

    @Test
    void existsById() {
        Long id = 1l;
        boolean exists = repository.existsById(id);

        assertTrue(exists);
    }

    @Test
    void existsById_NotexistingID() {
        Long id = 5l;
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
        Album album = Album.builder()
                .id(3L)
                .nombre("Altar")
                .anio(1989)
                .banda("Morbid Angel")
                .genero("Death Metal")
                .precio(20.89)
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
        Album album = Album.builder().id(1L).build();

        Album savedAlbum = repository.save(album);
        var all = repository.findAll();

        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(album, savedAlbum),
                () -> assertEquals(2, all.size())
        );
    }

    @Test
    void deleteById() {
        Long id = 1l;
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
                () -> assertEquals(1,all.size()),
                () ->assertFalse(repository.existsByUuid(uuid))
        );
    }



    @Test
    void nextId() {
        Long nextID = repository.nextId();
        var all =  repository.findAll();

        assertAll("nextId",
                () -> assertEquals(3l,nextID),
                () -> assertEquals(2,all.size())
        );
    }
}