package com.carlosvc.repaso_springboot.Albumes.services;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Albumes.mappers.AlbumMapper;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.services.DiscograficasService;
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
    private final Discografica disco = Discografica.builder().nombre("black light").build();
    private final Album album1 = Album.builder()
            .id(1l)
            .nombre("The End")
            .anio(1993)
            .banda("Ad Hominem")
            .genero("Black Metal")
            .precio(15.90)
            .discografica(disco)
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
            .discografica(disco)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("b36835eb-e56a-4023-b058-52bfa600fee5"))
            .build();

    private AlbumResponseDto albumResponseDto;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private DiscograficasService discoService;

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
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDtoList(expectedAlbums);
        when(albumRepository.findAll()).thenReturn(expectedAlbums);

        List<AlbumResponseDto> actualAlbumsResponses = albumesServices.findAll(null,null);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository,times(1)).findAll();
    }

    @Test
    void  findAll_returnAlbumsByNombre_WhenNombreIsProvided() {
        String nombre = "The End";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDtoList(expectedAlbums);
        when(albumRepository.findByNombre(nombre)).thenReturn(expectedAlbums);

        List<AlbumResponseDto>  actualAlbumsResponses = albumesServices.findAll(nombre,null);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository, only()).findByNombre(nombre);
    }

    @Test
    void  findAll_returnAlbumsByBanda_WhenDiscograficaIsProvided() {
        String discografica = "Black Light";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDtoList(expectedAlbums);
        when(albumRepository.findByDiscograficaContainsIgnoreCase(discografica.toLowerCase())).thenReturn(expectedAlbums);

        List<AlbumResponseDto>  actualAlbumsResponses = albumesServices.findAll(null,discografica);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository, only()).findByDiscograficaContainsIgnoreCase(discografica.toLowerCase());
    }

    @Test
    void  findAll_returnAlbums_WhenBandaAndNombreAreProvided() {
        String nombre = "The End";
        String discografica = "Black Light";
        List<Album> expectedAlbums = List.of(album1);
        List<AlbumResponseDto> expectedAlbumResponseDtos = albumMapper.toAlbumResponseDtoList(expectedAlbums);
        when(albumRepository.findByNombreAndDiscograficaContainsIgnoreCase(nombre,discografica)).thenReturn(expectedAlbums);

        List<AlbumResponseDto>  actualAlbumsResponses = albumesServices.findAll(nombre,discografica);

        assertIterableEquals(expectedAlbumResponseDtos, actualAlbumsResponses);

        verify(albumRepository).findByNombreAndDiscograficaContainsIgnoreCase(nombre,discografica);
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
        Long id = 3l;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());


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
                .discografica("Black Light")
                .build();

        Album expectedAlbum = Album.builder()
                .id(1l)
                .nombre("Opferblut")
                .anio(2004)
                .banda("Abigor")
                .genero("Black Metal")
                .precio(19.90)
                .discografica(disco)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
        AlbumResponseDto expectedAlbumResponseDto = albumMapper.toAlbumResponseDto(expectedAlbum);

        when(discoService.findByNombre(albumCreateDto.getDiscografica())).thenReturn(disco);
        when(albumRepository.save(any(Album.class))).thenReturn(expectedAlbum);

        AlbumResponseDto actualAlbumResponse = albumesServices.save(albumCreateDto);

        assertEquals(expectedAlbumResponseDto, actualAlbumResponse);


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
        Long id = 6l;
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                .anio(1800)
                .build();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> albumesServices.update(id,albumUpdateDto))
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessage("Album con el id: " + id + " no ha sido encontrado");

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
        Long id = 6l;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumesServices.deleteById(id))
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessage("Album con el id: " + id + " no ha sido encontrado");

        verify(albumRepository,never()).deleteById(id);
    }
}