package com.carlosvc.repaso_springboot.Discograficas.services;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.excepcions.DiscograficaConfictExcepcion;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.repositories.DiscograficasRepository;
import com.carlosvc.repaso_springboot.Discograficas.mappers.DiscograficaMappers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscograficasServicesIMPLTest {
    private final Discografica discografica = Discografica.builder().id(1L).nombre("Black Light").build();
    private final DiscograficaRequestDTO discograficaRequestDTO = DiscograficaRequestDTO.builder().nombre("Black Light").build();

    @Mock
    private DiscograficasRepository discograficasRepository;

    @Mock
    private DiscograficaMappers discograficaMappers;

    @InjectMocks
    private DiscograficasServicesIMPL discograficasServicesIMPL;

    @Test
    void findAll() {
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(List.of(discografica));
        when(discograficasRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(page);

        var res = discograficasServicesIMPL.findAll(Optional.empty(),Optional.empty(),pageable);
        assertAll("findAll",
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty()));

        verify(discograficasRepository, times(1)).findAll(any(Specification.class),any(Pageable.class));

    }


    @Test
    void findByNombre() {
        when(discograficasRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(discografica));

        var res = discograficasServicesIMPL.findByNombre("Black Light");

        assertAll("findByNombre",
                () -> assertNotNull(res),
                ()-> assertEquals("Black Light", res.getNombre()));

        verify(discograficasRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    }

    @Test
    void findById() {
        when(discograficasRepository.findById(anyLong())).thenReturn(Optional.of(discografica));

        var res = discograficasServicesIMPL.findById(1L);

        assertAll("findById",
                () -> assertNotNull(res),
                ()-> assertEquals("Black Light", res.getNombre()));
        verify(discograficasRepository, times(1)).findById(anyLong());
    }

    @Test
    void save() {
        when(discograficasRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(discograficaMappers.toDiscografica(any(DiscograficaRequestDTO.class))).thenReturn(discografica);
        when(discograficasRepository.save(any(Discografica.class))).thenReturn(discografica);

        discograficasServicesIMPL.save(discograficaRequestDTO);

        assertAll("save",
                () -> assertNotNull(discografica),
                () -> assertEquals("Black Light", discografica.getNombre()));

        verify(discograficasRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(discograficasRepository, times(1)).save(any(Discografica.class));
    }

    @Test
    public void testSaveConflict() {
        // Arrange
        when(discograficasRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(discografica));

        // Act
        var res = assertThrows(DiscograficaConfictExcepcion.class,
                () -> discograficasServicesIMPL.save(discograficaRequestDTO));

        // Assert
        assertAll("saveConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una discografica con el nombre: Black Light", res.getMessage())
        );

        // Verify
        verify(discograficasRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(discograficasRepository, times(0)).save(any(Discografica.class));
    }


    @Test
    void update() {
        when(discograficasRepository.findById(anyLong())).thenReturn(Optional.of(discografica));
        when(discograficasRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(discografica));
        when(discograficaMappers.toDiscografica(any(DiscograficaRequestDTO.class), any(Discografica.class))).thenReturn(discografica);
        when(discograficasRepository.save(any())).thenReturn(discografica);

        discograficasServicesIMPL.update(1L, discograficaRequestDTO);

        assertAll("update",
                () -> assertNotNull(discografica),
                () -> assertEquals("Black Light", discografica.getNombre()));

        verify(discograficasRepository, times(1)).findById(anyLong());
        verify(discograficasRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(discograficasRepository, times(1)).save(any(Discografica.class));

    }

    @Test
    public void testUpdateConflict() {
        // Arrange
        when(discograficasRepository.findById(anyLong())).thenReturn(Optional.of(discografica));
        when(discograficasRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(discografica));

        // Act, el id no debe ser igual, no se puede actualizar, porqe ya existe
        var res = assertThrows(DiscograficaConfictExcepcion.class,
                () -> discograficasServicesIMPL.update(2L, discograficaRequestDTO));

        // Assert
        assertAll("updateConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una discografica con el nombre: Black Light", res.getMessage())
        );

        // Verify
        verify(discograficasRepository, times(1)).findById(anyLong());
        verify(discograficasRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(discograficasRepository, times(0)).save(any(Discografica.class));
    }

    @Test
    void deleteById() {
        when(discograficasRepository.findById(anyLong())).thenReturn(Optional.of(discografica));
        when(discograficasRepository.existsAlbumById(anyLong())).thenReturn(false);

        discograficasServicesIMPL.deleteById(1L);

        assertAll("deleteById",
                () -> assertNotNull(discografica),
                () -> assertEquals("Black Light", discografica.getNombre()));

        verify(discograficasRepository, times(1)).findById(anyLong());
        verify(discograficasRepository, times(1)).existsAlbumById(anyLong());
        verify(discograficasRepository, times(1)).deleteById(anyLong());
    }
}