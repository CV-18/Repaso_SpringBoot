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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        when(albumesService.findAll(null, null)).thenReturn(albumResponses);

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(albumResponses.size());
                    assertThat(json).extractingPath("$[0]").convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto1);
                    assertThat(json).extractingPath("$[1]").convertTo(AlbumResponseDto.class).isEqualTo(albumResponseDto2);
                });
        verify(albumesService, times(1)).findAll(null, null);
    }

    // ... (Mantén los tests de búsqueda getAllBy... que estaban bien)

    @Test
    void getById_ReturnJSON_InvalidIDProvided() {
        Long id  = albumResponseDto1.getId();
        when(albumesService.findById(anyLong())).thenThrow(new AlbumNotFoundExcepcion(id));

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlbumResponseDto.class)
                .isEqualTo(albumResponseDto1);

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

        // CORREGIDO: .post()
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
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
                    "precio": 17.80
                }
                """;

        // CORREGIDO: .post()
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errors", path -> {
                    Map<String, String> errors = (Map<String, String>) path;
                    assertThat(errors).containsKey("nombre");
                    assertThat(errors).containsKey("anio");
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

        // CORREGIDO: .put()
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
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
        Long id = 3L;
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

        // CORREGIDO: .put()
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasFailed()
                .failure()
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no ha sido encontrado");

        verify(albumesService, only()).update(anyLong(), any());
    }

    @Test
    void delete() {
        Long id = 2L;
        doNothing().when(albumesService).deleteById(anyLong());

        // CORREGIDO: .delete()
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(albumesService, only()).deleteById(anyLong());
    }

    @Test
    void delete_InvalidIdProvided() {
        Long id = 3L;
        doThrow(new AlbumNotFoundExcepcion(id)).when(albumesService).deleteById(anyLong());

        // CORREGIDO: .delete()
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(AlbumNotFoundExcepcion.class)
                .hasMessageContaining("no  encontrado");

        verify(albumesService, only()).deleteById(anyLong());
    }
}