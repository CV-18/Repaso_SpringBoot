package com.carlosvc.repaso_springboot.Discograficas.repositories;

import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class DiscograficasRepositoryTest {
    private final Discografica discografica = Discografica.builder().nombre("Altar").build();

    @Autowired
    private DiscograficasRepository repositorio;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Insertamos un titular antes de cada test
        entityManager.persist(discografica);
        // Sincroniza los cambios en los objetos del contexto de persistencia con la BD
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Discografica> titulares = repositorio.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(titulares),
                () -> assertFalse(titulares.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        // Act
        List<Discografica> discograficas = repositorio.findByNombreContainingIgnoreCase("Altar");

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(discograficas),
                () -> assertFalse(discograficas.isEmpty()),
                () -> assertEquals("Altar", discograficas.getFirst().getNombre())
        );
    }

    @Test
    void findById() {
        // Act
        Discografica discografica = repositorio.findById(1L).orElse(null);

        // Assert
        assertAll("findById",
                () -> assertNotNull(discografica),
                () -> assertEquals("Altar", discografica.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Discografica discografica = repositorio.findById(100L).orElse(null);

        // Assert
        assertNull(discografica);
    }

    @Test
    void save() {
        // Act
        Discografica discografica = repositorio.save(Discografica.builder().nombre("Black Madness").build());

        // Assert
        assertAll("save",
                () -> assertNotNull(discografica),
                () -> assertEquals("Black Madness", discografica.getNombre())
        );
    }

    @Test
    void update() {
        // Act
        var discograficaExistente = repositorio.findById(1L).orElse(null);
        Discografica discograficaActualizar = Discografica.builder()
                .id(discograficaExistente.getId())
                .nombre("Black Madness").build();
        Discografica discograficaActualizada = repositorio.save(discograficaActualizar);

        // Assert
        assertAll("update",
                () -> assertNotNull(discograficaActualizada),
                () -> assertEquals("Black Madness", discograficaActualizada.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        var discograficaBorrar = repositorio.findById(1L).orElse(null);
        repositorio.delete(discograficaBorrar);
        Discografica discograficaBorrada = repositorio.findById(1L).orElse(null);

        // Assert
        assertNull(discograficaBorrada);
    }

    // Para comprobar la diferencia entre usar FetchType.EAGER o LAZY en la relación de titular con tarjetas
    // hay que añadir o quitar fetch = FetchType.EAGER a la anotación @OneToMany.  No se puede hacer por código.
    // En este tipo de relación la opción por defecto es LAZY.
    @Test
    void test_FetchType_EAGER_vs_LAZY() {
        // Vacía la cache del contexto de persistencia (L1 Cache) para poder ver todas
        // las consultas a la BD en la consola
        entityManager.clear();

        Discografica discografica = repositorio.findById(1L).orElse(null);
        assertNotNull(discografica);
    }

}