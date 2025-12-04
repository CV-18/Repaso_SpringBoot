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

// Reseteamos la base de datos para partir de una situación conocida
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
// Vamos a probar el repositorio, pero moqueamos la base de datos JPA
@DataJpaTest
class AlbumRepositoryImplTest {

    private final Discografica discografica1 = Discografica.builder().nombre("Black Light").build();
    private final Discografica discografica2 = Discografica.builder().nombre("The Reaper").build();

    private final Album album1 = Album.builder()
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
    private AlbumRepository repositorio;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Vamos a salvar las discográficas primero
        entityManager.persist(discografica1);
        entityManager.persist(discografica2);
        // Vamos a salvar dos álbumes
        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Album> albumes = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(albumes),
                () -> assertEquals(2, albumes.size())
        );
    }

    @Test
    void findAllByNombre() {
        // Act
        String nombre = "The End";
        List<Album> albumes = repositorio.findByNombre(nombre);

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(nombre, albumes.getFirst().getNombre())
        );
    }

    @Test
    void findAllByDiscografica() {
        // Act
        String discograficaNombre = "Black Light";
        // Nota: usamos toLowerCase() porque tu query JPQL usa LOWER()
        List<Album> albumes = repositorio.findByDiscograficaContainsIgnoreCase(discograficaNombre.toLowerCase());

        // Assert
        assertAll("findAllByDiscografica",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(discograficaNombre, albumes.getFirst().getDiscografica().getNombre())
        );
    }

    @Test
    void findAllByNombreAndDiscografica() {
        // Act
        String nombre = "Bergtatt";
        String discograficaNombre = "The Reaper";
        // Nota: usamos toLowerCase() en la discográfica porque tu query JPQL usa LOWER()
        List<Album> albumes = repositorio.findByNombreAndDiscograficaContainsIgnoreCase(nombre, discograficaNombre.toLowerCase());

        // Assert
        assertAll("findAllByNombreAndDiscografica",
                () -> assertNotNull(albumes),
                () -> assertEquals(1, albumes.size()),
                () -> assertEquals(nombre, albumes.getFirst().getNombre()),
                () -> assertEquals(discograficaNombre, albumes.getFirst().getDiscografica().getNombre())
        );
    }

    @Test
    void findById_existingId_returnsOptionalWithAlbum() {
        // Act
        Long id = album1.getId(); // Usamos el ID generado tras persistir
        Optional<Album> optionalAlbum = repositorio.findById(id);

        // Assert
        assertAll("findById_existingId_returnsOptionalWithAlbum",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(id, optionalAlbum.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        // Act
        Long id = 99L;
        Optional<Album> optionalAlbum = repositorio.findById(id);

        // Assert
        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnsOptionalWithAlbum() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Album> optionalAlbum = repositorio.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_existingUuid_returnsOptionalWithAlbum",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isPresent()),
                () -> assertEquals(uuid, optionalAlbum.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnsEmptyOptional() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Album> optionalAlbum = repositorio.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_nonExistingUuid_returnsEmptyOptional",
                () -> assertNotNull(optionalAlbum),
                () -> assertTrue(optionalAlbum.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnsTrue() {
        // Act
        Long id = album1.getId();
        boolean exists = repositorio.existsById(id);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // Act
        Long id = 99L;
        boolean exists = repositorio.existsById(id);

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByUuid_existingUuid_returnsTrue() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repositorio.existsByUuid(uuid);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUuid_nonExistingUuid_returnsFalse() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repositorio.existsByUuid(uuid);

        // Assert
        assertFalse(exists);
    }

    @Test
    void save_notExists() {
        // Arrange
        Album album = Album.builder()
                .nombre("Altar")
                .anio(1989)
                .banda("Morbid Angel")
                .genero("Death Metal")
                .precio(20.89)
                .discografica(discografica1) // Reutilizamos una discográfica existente
                .build();

        // Act
        Album savedAlbum = repositorio.save(album);
        var all = repositorio.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(album.getNombre(), savedAlbum.getNombre()),
                () -> assertEquals(3, all.size())
        );
    }

    @Test
    void save_butExists() {
        // Arrange
        Long id = album1.getId();
        Album albumExistente = Album.builder()
                .id(id)
                .nombre("The End")
                .anio(1993)
                .banda("Ad Hominem")
                .genero("Black Metal")
                .precio(30.00) // Cambiamos el precio
                .discografica(discografica1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478"))
                .build();

        // Act
        Album savedAlbum = repositorio.save(albumExistente);
        var all = repositorio.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedAlbum),
                () -> assertEquals(30.00, savedAlbum.getPrecio()),
                () -> assertTrue(repositorio.existsById(id)),
                () -> assertEquals(2, all.size()) // El tamaño sigue siendo 2, no se creó uno nuevo
        );
    }

    @Test
    void deleteById_existingId() {
        // Act
        Long id = album1.getId();
        repositorio.deleteById(id);
        var all = repositorio.findAll();

        // Assert
        assertAll("deleteById_existingId",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(repositorio.existsById(id))
        );
    }

    @Test
    void deleteByUuid_existingUuid() {
        // Act
        UUID uuid = UUID.fromString("57727bc2-0c1c-494e-bbaf-e952a778e478");
        repositorio.deleteByUuid(uuid);
        var all = repositorio.findAll();

        // Assert
        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(repositorio.existsByUuid(uuid))
        );
    }
}