package com.carlosvc.repaso_springboot.Albumes.controllers;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.excepcions.AlbumNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Albumes.services.AlbumesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc

class AlbumesRestControllerTest {
    private final String ENDPOINT = "/api/v1/Albumes";

    private final AlbumResponseDto albumResponseDto1 = AlbumResponseDto.builder()
            .id(1L)
            .nombre("The End")
            .anio(1993)
            .banda("Ad Hominem")
            .genero("Black Metal")
            .discografica("Black Light")
            .precio(15.90)
            .build();

    private final AlbumResponseDto albumResponseDto2 = AlbumResponseDto.builder()
            .id(2L)
            .nombre("Bergtatt")
            .anio(1994)
            .banda("Ulver")
            .genero("Black Metal")
            .discografica("The Reaper")
            .precio(14.90)
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AlbumesService albumesService;

    @Test
    void getAll() {
        var albumResponses = List.of(albumResponseDto1, albumResponseDto2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(albumResponses);
        when(albumesService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto2);
                });

        verify(albumesService, times(1))
                .findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);    }

    @Test
    void getAllByNombre() {
        var albumResponses = List.of(albumResponseDto2);
        String queryString = "?nombre=" + albumResponseDto2.getNombre();
        Optional<String> nombre = Optional.of(albumResponseDto2.getNombre());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(albumResponses);
        when(albumesService.findAll(nombre, Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto2);
                });

        verify(albumesService, times(1))
                .findAll(nombre, Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByDiscografica() {
        var albumResponses = List.of(albumResponseDto2);
        String queryString = "?discografica=" + albumResponseDto2.getDiscografica();
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(albumResponses);
        when(albumesService.findAll(any(), any(), any(), any()))
                .thenReturn(page);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto2);
                });

        verify(albumesService, only())
                .findAll(any(), any(), any(), any());
    }

    @Test
    void getAllByNombreAndDiscografica() {
        var albumResponses = List.of(albumResponseDto2);
        String queryString = "?nombre=" + albumResponseDto2.getNombre() + "&"
                + "discografica=" + albumResponseDto2.getDiscografica();
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(albumResponses);
        when(albumesService.findAll(any(), any(), any(), any())).thenReturn(page);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto2);
                });

        verify(albumesService, only()).findAll(any(), any(), any(), any());
    }

    @Test
    void getById_shouldReturnJsonWithTarjeta_whenValidIdProvided() {
        Long id = albumResponseDto1.getId();
        when(albumesService.findById(id)).thenReturn(albumResponseDto1);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumResponseDto1);

        verify(albumesService, only()).findById(anyLong());

    }

    @Test
    void getById_ReturnJSON_InvalidIDProvided() {
        Long id  = 3L;
        when(albumesService.findById(anyLong())).thenThrow(new AlbumNotFoundExcepcion(id));

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no encontrado");

        verify(albumesService, only()).findById(anyLong());
    }

    @Test
    void create() {
        String requestBody = """
                {
                    "nombre": "Non Serviam",
                    "anio": 1994,
                    "banda": "Rotting Christ",
                    "genero": "Black Metal",
                    "precio": 17.80,
                    "discografica": "Test Records"
                }
                """;

        var albumSaved = AlbumResponseDto.builder()
                .id(1L)
                .nombre("Non Serviam")
                .anio(1994)
                .banda("Rotting Christ")
                .genero("Black Metal")
                .precio(17.80)
                .build();

        when(albumesService.save(any(AlbumCreateDto.class))).thenReturn(albumSaved);


        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumSaved);

        verify(albumesService, only()).save(any(AlbumCreateDto.class));
    }

    @Test
    void create_BadRequest() {
        String requestBody = """
                {
                    "nombre": "",
                    "anio": 1800,
                    "banda": "Rotting Christ",
                    "genero": "Black Metal",
                    "precio": 17.80,
                    "discografica": ""
                }
                """;


        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errors", path -> {
                    assertThat(path).hasFieldOrProperty("nombre");
                    assertThat(path).hasFieldOrProperty("anio");
                    assertThat(path).hasFieldOrProperty("discografica");
                });

        verify(albumesService, never()).save(any(AlbumCreateDto.class));
    }

    @Test
    void update() {
        Long id = 2L;
        String requestBody = """
                {
                    "nombre": "Bergtatt",
                    "anio": 1994,
                    "banda": "Ulver",
                    "genero": "Black Metal",
                    "precio": 5.00,
                    "discografica": "Black Light"
                }
            """;

        var albumSaved = AlbumResponseDto.builder()
                .id(id)
                .nombre("Bergtatt")
                .anio(1994)
                .banda("Ulver")
                .genero("Black Metal")
                .precio(5.00)
                .build();

        when(albumesService.update(anyLong(), any(AlbumUpdateDto.class))).thenReturn(albumSaved);

        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumSaved);

        verify(albumesService, only()).update(anyLong(), any(AlbumUpdateDto.class));
    }

    @Test
    void update_InvalidIdProvided() {
        Long id = 4L;
        String requestBody = """
                {
                    "nombre": "Bergtatt",
                    "anio": 1994,
                    "banda": "Ulver",
                    "genero": "Black Metal",
                    "precio": 5.00,
                    "discografica": "Black Light"
                }
            """;
        when(albumesService.update(anyLong(), any(AlbumUpdateDto.class))).thenThrow(new AlbumNotFoundExcepcion(id));

        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no encontrado");

        verify(albumesService, only()).update(anyLong(), any(AlbumUpdateDto.class));
    }

    @Test
    void delete() {
        Long id = 2L;
        doNothing().when(albumesService).deleteById(anyLong());

        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .with(csrf())
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(albumesService, only()).deleteById(anyLong());
    }

    @Test
    void delete_InvalidIdProvided() {
        Long id = 3L;
        doThrow(new AlbumNotFoundExcepcion(id)).when(albumesService).deleteById(anyLong());


        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .with(csrf())
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no encontrado");

        verify(albumesService, only()).deleteById(anyLong());
    }
}
