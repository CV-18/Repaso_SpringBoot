package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
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


import static org.assertj.core.api.Assertions.*;
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
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("696969bc2-0c1c-494e-haf-e952a778e478"))
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
            .uuid(UUID.fromString("696969bc2-0c1c-494e-u9af-e952a778e478"))
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
        List<Album> expectedAlbums = Arrays.asList(album1,album2);
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
    void findAll_WhenGeneroIsProvided() {
        String genero = "Black Metal";
        AlbumResponseDto expectedAlbumResponseDto = albumResponseDto;
        when(albumRepository.findByGenero(genero)).thenReturn(Optional.of(album1));

        AlbumResponseDto actualResponseDTO = albumesServices.findByGenero(genero);

        assertSame(expectedAlbumResponseDto, actualResponseDTO);

        verify(albumRepository).findByGenero(genero);

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
    void findbyUuid_returnAlbum_ValidUUIDProvided() {
        UUID expectedUUID = album2.getUuid();
        AlbumResponseDto expectedAlbumResponseDto = albumResponseDto;
        when(albumRepository.findByUuid(expectedUUID)).thenReturn(Optional.of(album1));

        AlbumResponseDto actualAlbumResponse = albumesServices.findbyUuid(expectedUUID.toString());

        assertEquals(expectedAlbumResponseDto, actualAlbumResponse);

        verify(albumRepository, only()).findByUuid(expectedUUID);

    }


    @Test
    void save_ValidAlbumCreateDTOProvided() {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
                .nombre("Opferblut")
                .anio(2004)
                .banda("Abigor")
                .genero("Black Metal")
                .precio(19.90)
                .build();

        Album expectedAlbum = Album.builder()
                .id(1l)
                .nombre("Opferblut")
                .anio(2004)
                .banda("Abigor")
                .genero("Black Metal")
                .precio(19.90)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
        AlbumResponseDto expectedAlbumResponseDto = albumMapper.toAlbumResponseDto(expectedAlbum);

        when(albumRepository.nextId()).thenReturn(1l);
        when(albumRepository.save(any(Album.class))).thenReturn(expectedAlbum);

        AlbumResponseDto actualAlbumResponse = albumesServices.save(albumCreateDto);

        assertEquals(expectedAlbumResponseDto, actualAlbumResponse);

        verify(albumRepository).nextId();
        verify(albumRepository).save(albumCaptor.capture());

        Album albumCapture = albumCaptor.getValue();
        assertEquals(expectedAlbumResponseDto.getNombre(), albumCapture.getNombre());
    }

    @Test
    void update_ReturnAlbum_WhenValidAlbumUpdateProvided() {
        Long id = 1l;
        Double precio = 20.90;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                .precio(precio)
                .build();
        Album albumUpdate = albumMapper.toAlbum(albumUpdateDto,album1);
        when(albumRepository.save(any(Album.class))).thenReturn(albumUpdate);

        albumResponseDto.setPrecio(precio);
        AlbumResponseDto expectedAlbumResponseDto = albumResponseDto;

        AlbumResponseDto actualAlbumResponse = albumesServices.update(id,albumUpdateDto);

        assertThat(actualAlbumResponse)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(expectedAlbumResponseDto);

        verify(albumRepository).findById(id);
        verify(albumRepository).save(any());

    }

    @Test
    void update_ReturnAlbum_WhenInvalidAlbumUpdateProvided() {
        Long id = 1l;
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                .anio(1998)
                .build();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> albumesServices.update(id,albumUpdateDto))
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessage("Album con el id: " + id + " no encontrado");

        verify(albumRepository).findById(id);
        verify(albumRepository,never()).save(any());
    }

    @Test
    void deleteById_DeleteAlbum_WhenValidIDProvided() {
        Long id = 1l;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        assertThatCode(() -> albumesServices.deleteById(id))
                .doesNotThrowAnyException();

        verify(albumRepository).deleteById(id);
    }

    @Test
    void deleteById_DeleteAlbum_WhenInValidIDProvided_ThrowsException() {
        Long id = 1l;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumesServices.deleteById(id))
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessage("Album con el id: " + id + " no encontrado");

        verify(albumRepository,never()).deleteById(id);
    }
}