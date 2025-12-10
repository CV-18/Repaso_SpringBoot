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
import com.carlosvc.repaso_springboot.config.websockets.WebSocketConfig;
import com.carlosvc.repaso_springboot.config.websockets.WebSocketHandler;
import com.carlosvc.repaso_springboot.websockets.notifications.mappers.AlbumNotificationMApper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
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
    private final Discografica disco = Discografica.builder().nombre("Black light").build();
    private final Album album1 = Album.builder()
            .id(1L)
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
            .id(2L)
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

    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumesServicesImpl albumesServices;

    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private AlbumNotificationMApper albumNotificationMApper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WebSocketHandler webSocketHandler;

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    @BeforeEach
    void setUp() {
        albumResponseDto = AlbumResponseDto.builder()
                .id(1L)
                .nombre("The End")
                .anio(1993)
                .banda("Ad Hominem")
                .genero("Black Metal")
                .precio(15.90)
                .discografica("Black Light")
                .build();
        lenient().when(albumMapper.toAlbumResponseDto(any(Album.class))).thenReturn(albumResponseDto);
        albumesServices.setWebSocketService(webSocketHandler);
    }


    @Test
    void findAll_returnAll_noParameters() {
        List<Album> expectedAlbums = Arrays.asList(album1,album2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Album> expectedPage = new PageImpl<>(expectedAlbums);
        when(albumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<AlbumResponseDto> actualPage = albumesServices.findAll(Optional.empty(),Optional.empty(),Optional.empty(),pageable);
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        verify(albumRepository,times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void  findAll_returnAlbumsByNombre_WhenNombreIsProvided() {
        Optional<String> nombre = Optional.of("Ad Hominem");
        List<Album> expectedAlbums = List.of(album1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Album> expectedPage = new PageImpl<>(expectedAlbums);
        when(albumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<AlbumResponseDto> actualPage = albumesServices.findAll(nombre,Optional.empty(),Optional.empty(), pageable);


        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        verify(albumRepository, only()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void  findAll_returnAlbumsByDiscografica_WhenDiscograficaIsProvided() {
        Optional<String> discografica = Optional.of("Black Light");
        List<Album> expectedAlbums = List.of(album1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Album> expectedPage = new PageImpl<>(expectedAlbums);
        when(albumRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<AlbumResponseDto> actualPage = albumesServices.findAll(Optional.empty(),discografica,Optional.empty(),pageable);

        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0));

        verify(albumRepository, only()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void  findAll_returnAlbums_WhenNombreAndDiscograficaAreProvided() {
        Optional<String> nombre = Optional.of("The End");
        Optional<String> discografica = Optional.of("Black Light");
        List<Album> expectedAlbums = List.of(album1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Album> expectedPage = new PageImpl<>(expectedAlbums);
        when(albumRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(expectedPage);

        Page<AlbumResponseDto> actualPage = albumesServices.findAll(nombre,discografica,Optional.empty(),pageable);
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0));

        verify(albumRepository, only()).findAll(any(Specification.class), any(Pageable.class));
    }



    @Test
    void findById_ValidIDProvided() {
        Long id = 1L;
        AlbumResponseDto expectedAlbumResponseDto = albumResponseDto;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        AlbumResponseDto actualAlbumResponseDto = albumesServices.findById(id);

        assertEquals(expectedAlbumResponseDto, actualAlbumResponseDto);

        verify(albumRepository, only()).findById(id);

    }

    @Test
    void findById_InvalidIDProvided() {
        Long id = 3L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());


        var ressult = assertThrows(AlbumNotFoundExcepcion.class, ()-> albumesServices.findById(id));
        assertThat(ressult.getMessage()).contains("no encontrado");

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
    void save_ValidAlbumCreateDTOProvided() throws IOException {
        AlbumCreateDto albumCreateDto = AlbumCreateDto.builder()
                .nombre("Opferblut")
                .anio(2004)
                .banda("Abigor")
                .genero("Black Metal")
                .precio(19.90)
                .discografica("Black Light")
                .build();

        Album expectedAlbum = Album.builder()
                .id(1L)
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
        
        AlbumResponseDto expectedAlbumResponseDto = AlbumResponseDto.builder()
                .nombre("Opferblut")
                .anio(2004)
                .banda("Abigor")
                .genero("Black Metal")
                .precio(19.90)
                .discografica("Black Light")
                .build();

        when(discoService.findByNombre(albumCreateDto.getDiscografica())).thenReturn(disco);
        when(albumMapper.toAlbum(any(AlbumCreateDto.class), any(Discografica.class))).thenReturn(expectedAlbum);
        when(albumMapper.toAlbumResponseDto(any(Album.class))).thenReturn(expectedAlbumResponseDto);
        when(albumRepository.save(any(Album.class))).thenReturn(expectedAlbum);
        doNothing().when(webSocketHandler).sendMessage(any());

        AlbumResponseDto actualAlbumResponse = albumesServices.save(albumCreateDto);

        assertEquals(expectedAlbumResponseDto, actualAlbumResponse);


        verify(albumRepository).save(albumCaptor.capture());

        Album albumCapture = albumCaptor.getValue();
        assertEquals(expectedAlbumResponseDto.getNombre(), albumCapture.getNombre());
    }

    @Test
    void update_ReturnAlbum_WhenValidAlbumUpdateProvided()throws IOException {
        Long id = 1L;
        Double precio = 20.90;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));

        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                .precio(precio)
                .build();
        
        Album albumUpdate = Album.builder().id(1L).precio(precio).build();
        
        when(albumMapper.toAlbum(any(AlbumUpdateDto.class), any(Album.class))).thenReturn(albumUpdate);
        when(albumRepository.save(any(Album.class))).thenReturn(albumUpdate);

        AlbumResponseDto expectedAlbumResponseDto = AlbumResponseDto.builder().id(1L).precio(precio).build();
        when(albumMapper.toAlbumResponseDto(any(Album.class))).thenReturn(expectedAlbumResponseDto);
        
        doNothing().when(webSocketHandler).sendMessage(any());

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
        Long id = 6L;
        AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                .anio(1800)
                .build();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> albumesServices.update(id,albumUpdateDto))
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no encontrado");

        verify(albumRepository).findById(id);
        verify(albumRepository,never()).save(any());
    }

    @Test
    void deleteById_DeleteAlbum_WhenValidIDProvided() throws  IOException {
        Long id = 1L;
        when(albumRepository.findById(id)).thenReturn(Optional.of(album1));
        doNothing().when(webSocketHandler).sendMessage(any());


        assertThatCode(() -> albumesServices.deleteById(id))
                .doesNotThrowAnyException();

        verify(albumRepository).deleteById(id);
    }

    @Test
    void deleteById_DeleteAlbum_WhenInValidIDProvided_ThrowsException() {
        Long id = 6L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumesServices.deleteById(id))
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no encontrado");

        verify(albumRepository,never()).deleteById(id);
    }
}