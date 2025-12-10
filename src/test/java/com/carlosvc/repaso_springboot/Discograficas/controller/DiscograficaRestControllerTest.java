package com.carlosvc.repaso_springboot.Discograficas.controller;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.excepcions.DiscograficaConfictExcepcion;
import com.carlosvc.repaso_springboot.Discograficas.excepcions.DiscograficaNotFoundExcepcion;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.services.DiscograficasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class DiscograficaRestControllerTest {
    private final String ENDPOINT = "/api/v1/Discograficas";

    private final Discografica discografica1 = Discografica.builder().id(1L).nombre("Black Light").build();
    private final Discografica discografica2 = Discografica.builder().id(2L).nombre("The Reaper").build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private DiscograficasService  discograficasService;






    @Test
    void getAll() {
        var discograficas = List.of(discografica1, discografica2);
        when(discograficasService.findAll(null)).thenReturn(discograficas);

        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.lenght()").isEqualTo(discograficas.size());
                    assertThat(json).extractingPath("$.[0]")
                            .convertTo(Discografica.class).usingRecursiveComparison().isEqualTo(discografica1);
                    assertThat(json).extractingPath("$.[1]")
                            .convertTo(Discografica.class).usingRecursiveComparison().isEqualTo(discografica2);
                });
        verify(discograficasService,times(1)).findAll(null);

    }

    @Test
    void getAllByNombre(){
        var discograficas = List.of(discografica2);
        String queryString = "?nombre=" + discografica2.getNombre();
        when(discograficasService.findAll(anyString())).thenReturn(discograficas);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
        .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.lenght()").isEqualTo(discograficas.size());
                    assertThat(json).extractingPath("$.[0]")
                            .convertTo(Discografica.class).usingRecursiveComparison().isEqualTo(discografica1);
                });
    }

    @Test
    void getById() {
        Long id = discografica1.getId();
        when(discograficasService.findById(id)).thenReturn(discografica1);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
        .hasStatusOk()
                .bodyJson()
                .convertTo(Discografica.class).usingRecursiveComparison().isEqualTo(discografica1);
    }

    @Test
    void create() {
        String requestBody = """
           {
              "nombre": "Madness"
           }
           """;

        var discograficaSaved = Discografica.builder()
                .id(1L)
                .nombre("Madness")
                .build();

        when(discograficasService.save(any(DiscograficaRequestDTO.class))).thenReturn(discograficaSaved);

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
        .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(Discografica.class)
                .isEqualTo(discograficaSaved);

        verify(discograficasService, only()).save(any(DiscograficaRequestDTO.class));
    }

    @Test
    void create_whenBadRequest() {
        // Arrange
        String requestBody = """
           {
              "nombre": null
           }
           """;

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));


        verify(discograficasService, never()).save(any(DiscograficaRequestDTO.class));

    }

    @Test
    void create_whenNombreExists() {
        // Arrange
        String requestBody = """
           {
              "nombre": "Black Light"
           }
           """;

        when(discograficasService.save(any(DiscograficaRequestDTO.class)))
                .thenThrow(new DiscograficaConfictExcepcion("Ya existe una discografica con el nombre Black Light"));


        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                // throws TitularesConflictEsception
                .hasFailed().failure()
                .isInstanceOf(DiscograficaConfictExcepcion.class)
                .hasMessageContaining("Ya existe una Discografica");


        verify(discograficasService, only()).save(any(DiscograficaRequestDTO.class));
    }

    @Test
    void update() {
        // Arrange
        Long id = 1L;
        String requestBody = """
           {
              "nombre": "BLACK LIGHT"
           }
           """;

        var discograficaSaved = Discografica.builder()
                .id(1L)
                .nombre("BLACK LIGHT")
                .build();

        when(discograficasService.update(anyLong(), any(DiscograficaRequestDTO.class))).thenReturn(discograficaSaved);

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT+ "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Discografica.class)
                .usingRecursiveComparison()
                .isEqualTo(discograficaSaved);

        verify(discograficasService, only()).update(anyLong(), any(DiscograficaRequestDTO.class));
    }

    @Test
    void update_shouldThrowTitularNotFound() {
        // Arrange
        Long id = 3L;
        String requestBody = """
           {
              "nombre": "JOSE"
           }
           """;
        when(discograficasService.update(anyLong(), any(DiscograficaRequestDTO.class))).thenThrow(new DiscograficaNotFoundExcepcion(id));

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws TarjetaNotFoundException
                .hasFailed().failure()
                .isInstanceOf(DiscograficaNotFoundExcepcion.class)
                .hasMessageContaining("no encontrado");

        // Verify
        verify(discograficasService, only()).update(anyLong(), any());
    }

    @Test
    void update_shouldThrowBadRequest() {
        Long id = 3L;
        String requestBody = """
           {
              "nombre": null
           }
           """;

        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));


        verify(discograficasService, never()).update(anyLong(), any(DiscograficaRequestDTO.class));
    }

    @Test
    void update_whenNombreExists() {
        Long id = 1L;
        String requestBody = """
           {
              "nombre": "Black Light"
           }
           """;

        when(discograficasService.update(anyLong(), any(DiscograficaRequestDTO.class)))
                .thenThrow(new DiscograficaConfictExcepcion("Ya existe una discografica con el nombre Black Light"));


        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                // throws TitularesConflictEsception
                .hasFailed().failure()
                .isInstanceOf(DiscograficaConfictExcepcion.class)
                .hasMessageContaining("Ya existe una discografica");

        verify(discograficasService, only()).update(anyLong(), any(DiscograficaRequestDTO.class));
    }

    @Test
    void delete() {
        Long id = 1L;
        doNothing().when(discograficasService).deleteById(anyLong());
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();
        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(discograficasService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowDiscograficaNotFound() {
        Long id = 5L;
        doThrow(new DiscograficaNotFoundExcepcion(id)).when(discograficasService).deleteById(anyLong());
        var result = mockMvcTester.delete()
                .uri(ENDPOINT+ "/" + id)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND);

        verify(discograficasService, only()).deleteById(anyLong());
    }


}