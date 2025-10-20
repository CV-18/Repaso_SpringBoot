package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Albumes.mappers.AlbumMapper;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Albumes.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumesServicesImplTest {
    private final Album album1 = Album.builder()
            .id(1l)
            .nombre("The End")
            .anio(1993)
            .banda("Ad Hominem")
            .genero("Black Metal")
            .precio(15.90)
            .createdAt(LocalDateTime.now())
            .updateAt(LocalDateTime.now())
            .uuid(UUID.fromString("696969bc2-0c1c-494e-hhaf-e952a778e478"))
            .build();

    private AlbumResponseDto albumResponseDto;

    @Mock
    private AlbumRepository albumRepository;

    @Spy
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumesServicesImpl albumesServices;

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    @BeforeEach
    void setUp() {
        albumResponseDto = albumMapper.toAlbumResponseDto(album1);
    }


    @Test
    void findAll_returnAll_noParameters() {
        List<Album> expectedAlbums = Arrays.asList(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDto(expectedAlbums);
        when(albumRepository.findAll()).thenReturn(expectedAlbums);

        List<AlbumResponseDto> actualAlbumsResponses = albumesServices.findAll(null,null);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository,times(1)).findAll();
    }

    @Test
    void  findAll_returnAlbumsByNombre_WhenNombreIsProvided() {
        String nombre = "The End";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDto(expectedAlbums);
        when(albumRepository.findAllByNombre(nombre)).thenReturn(expectedAlbums);

        List<AlbumResponseDto>  actualAlbumsResponses = albumesServices.findAll(nombre,null);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository, only()).findAllByNombre(nombre);
    }

    @Test
    void  findAll_returnAlbumsByBanda_WhenBandaIsProvided() {
        String banda = "Ad  Hominem";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDto(expectedAlbums);
        when(albumRepository.findAllByBanda(banda)).thenReturn(expectedAlbums);

        List<AlbumResponseDto>  actualAlbumsResponses = albumesServices.findAll(null,banda);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository, only()).findAllByBanda(banda);
    }

    @Test
    void  findAll_returnAlbums_WhenBandaAndNombreAreProvided() {
        String nombre = "The End";
        String banda = "Ad  Hominem";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDto(expectedAlbums);
        when(albumRepository.findAllByNombreAndBanda(nombre,banda)).thenReturn(expectedAlbums);

        List<AlbumResponseDto>  actualAlbumsResponses = albumesServices.findAll(nombre,banda);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository, only()).findAllByBanda(banda);
    }

    @Test
    void findById_ValidIDProvided() {
        Long id = 1l;
        AlbumResponseDto expectedAlbumResponseDto = albumResponseDto;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        AlbumResponseDto actualAlbumResponseDto = albumesServices.findById(id);

        assertEquals(expectedAlbumResponseDto, actualAlbumResponseDto);

        verify(albumRepository, only()).findById(id);

    }

    @Test
    void findById_InvalidIDProvided() {
        Long id = 1l;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        AlbumResponseDto actualAlbumResponseDto = albumesServices.findById(id);

        var ressult = assertThrows(AlbumNotFoundExcepcion.class, ()-> albumesServices.findById(id));
        assertEquals("Album con el id: " + id + " no ha sido encontrado", ressult.getMessage());

        verify(albumRepository).findById(id);

    }


    @Test
    void findbyUuid() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }
}