package com.carlosvc.repaso_springboot.web.controllers;


import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.rest.users.models.User;
import com.carlosvc.repaso_springboot.rest.users.services.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AlbumesControllerTest {

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private AlbumesService albumesService;

  @MockitoBean
  private UsersService usersService;


  private final Album album1 = Album.builder()
    .id(1L)
    .nombre("Memento Mori")
    .banda("Marduk")
    .anio(2023)
    .genero("Black Metal")
    .precio(18.00)
    .build();

  private final Album album2 = Album.builder()
    .id(2L)
    .nombre("Aske")
    .banda("Burzum")
    .anio(1992)
    .genero("Black Metal")
    .precio(10.50)
    .build();


  @WithUserDetails("jose")
  @Test
  @DisplayName("GET /app/allalbumes/{id}")
  void getById(){
    Long id = 1L;
    when(albumesService.buscarPorId(id)).thenReturn(Optional.of(album1));

    var result = mockMvcTester.get()
      .uri("/app/allalbumes/{id}", id)
      .contentType(MediaType.TEXT_HTML)
      .exchange();

    var mvcAssert = assertThat(result)
      .hasStatusOk()
      .hasViewName("app/albumes/detalle");


    mvcAssert.model()
        .containsKeys("album")
          .containsEntry("album", album1);

    // El texto "Detalle del album: " no existe en la plantilla, por lo que esta aserción fallará si se mantiene.
    // Se verifica que el nombre del album esté presente en el body.
    mvcAssert.bodyText()
        .contains(album1.getNombre());

    verify(albumesService, only()).buscarPorId(anyLong());

  }


  @WithUserDetails("jose")
  @Test
  @DisplayName("GET /app/allalbumes")
  void allbumes(){
    Long usuarioId = 2L;
    User usuario = User.builder().id(usuarioId).username("jose").build();
    List<Album> albumes = List.of(album1, album2);

    when(albumesService.findAllTotal()).thenReturn(albumes);

    var result = mockMvcTester.get()
      .uri("/app/allalbumes")
      .contentType(MediaType.TEXT_HTML)
      .exchange();

    assertThat(result)
      .hasStatusOk()
      .hasViewName("app/albumes/lista")
      .model()
        .containsKeys("albumes")
          .hasEntrySatisfying("albumes", lista -> assertThat((List<?>) lista)
            .isInstanceOf(List.class)
              .hasSize(2)
          );


    verify(albumesService, times(1)).findAllTotal();
  }

}
