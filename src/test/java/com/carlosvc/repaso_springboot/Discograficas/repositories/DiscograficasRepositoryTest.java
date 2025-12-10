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
    private final Discografica discografica2 = Discografica.builder().nombre("Black Light").build();

    @Autowired
    private DiscograficasRepository repositorio;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.persist(discografica);
        entityManager.persist(discografica2);
        entityManager.flush();
    }

    @Test
    void findAll() {
        List<Discografica> titulares = repositorio.findAll();

        assertAll("findAll",
                () -> assertNotNull(titulares),
                () -> assertFalse(titulares.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        List<Discografica> discograficas = repositorio.findByNombreContainingIgnoreCase("Altar");

        assertAll("findAllByNombre",
                () -> assertNotNull(discograficas),
                () -> assertFalse(discograficas.isEmpty()),
                () -> assertEquals("Altar", discograficas.getFirst().getNombre())
        );
    }

    @Test
    void findById() {
        Discografica discografica2 = repositorio.findById(2L).orElse(null);

        assertAll("findById",
                () -> assertNotNull(discografica2),
                () -> assertEquals("Black Light", discografica2.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        Discografica discografica = repositorio.findById(100L).orElse(null);

        assertNull(discografica);
    }

    @Test
    void save() {
        Discografica discografica = repositorio.save(Discografica.builder().nombre("Black Madness").build());

        assertAll("save",
                () -> assertNotNull(discografica),
                () -> assertEquals("Black Madness", discografica.getNombre())
        );
    }

    @Test
    void update() {
        var discograficaExistente = repositorio.findById(1L).orElse(null);
        Discografica discograficaActualizar = Discografica.builder()
                .id(discograficaExistente.getId())
                .nombre("Black Madness").build();
        Discografica discograficaActualizada = repositorio.save(discograficaActualizar);

        assertAll("update",
                () -> assertNotNull(discograficaActualizada),
                () -> assertEquals("Black Madness", discograficaActualizada.getNombre())
        );
    }

    @Test
    void delete() {
        var discograficaBorrar = repositorio.findById(1L).orElse(null);
        repositorio.delete(discograficaBorrar);
        Discografica discograficaBorrada = repositorio.findById(1L).orElse(null);

        assertNull(discograficaBorrada);
    }


    @Test
    void test_FetchType_EAGER_vs_LAZY() {
        entityManager.clear();

        Discografica discografica = repositorio.findById(1L).orElse(null);
        assertNotNull(discografica);
    }

}