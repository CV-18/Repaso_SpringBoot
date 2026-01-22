package com.carlosvc.repaso_springboot.Discograficas.mappers;

import com.carlosvc.repaso_springboot.rest.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.rest.Discograficas.mappers.DiscograficaMappers;
import com.carlosvc.repaso_springboot.rest.Discograficas.models.Discografica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscograficaMappersTest {
    private final Discografica discografica = Discografica.builder().id(1L).nombre("Black Light").build();

    private final DiscograficaMappers discograficaMappers = new DiscograficaMappers();

    private final DiscograficaRequestDTO discograficaRequestDTO = DiscograficaRequestDTO.builder().nombre("Black Light").build();


    @Test
    void whenToDiscografica_thenReturnDiscografica() {
        Discografica mappedDiscografica = discograficaMappers.toDiscografica(discograficaRequestDTO);
        assertAll("whenToDiscografica_thenReturnDiscografica",
                () -> assertEquals(discograficaRequestDTO.getNombre(),mappedDiscografica.getNombre()));
    }

    @Test
    void whenToDiscograficaWithExistingDiscografica_thenReturnUpdatedDiscografica() {
        Discografica updatedDiscografica = discograficaMappers.toDiscografica(discograficaRequestDTO, discografica);

        assertAll("whenToDiscograficaWithExistingDiscografica_thenReturnUpdatedDiscografica",
                () -> assertEquals(discograficaRequestDTO.getNombre(),updatedDiscografica.getNombre()));
    }
}