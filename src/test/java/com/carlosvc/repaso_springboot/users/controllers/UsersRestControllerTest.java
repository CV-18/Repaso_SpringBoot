package com.carlosvc.repaso_springboot.users.controllers;

import com.carlosvc.repaso_springboot.rest.users.dto.UserInfoResponse;
import com.carlosvc.repaso_springboot.rest.users.dto.UserRequest;
import com.carlosvc.repaso_springboot.rest.users.dto.UserResponse;
import com.carlosvc.repaso_springboot.rest.users.exceptions.UserNotFound;
import com.carlosvc.repaso_springboot.rest.users.services.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
@SpringBootTest
@AutoConfigureMockMvc
class UsersRestControllerTest {

  private final String ENDPOINT = "/api/v1/users";

  private final UserResponse userResponse = UserResponse.builder()
      .id(99L)
      .nombre("test")
      .apellidos("test")
      .username("test")
      .email("test@test.com")
      .build();
  private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
      .id(99L)
      .nombre("test")
      .apellidos("test")
      .username("test")
      .email("test@test.com")
      .build();

  @Autowired
  MockMvcTester mockMvcTester;

  @MockitoBean
  private UsersService usersService;

  @Test
  @WithAnonymousUser
  void NotAuthenticated() {
    var result = mockMvcTester.get()
        .uri(ENDPOINT)
        .exchange();

    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }

  @Test
  void findAll() {
    var userResponses = List.of(userResponse);
    Page<UserResponse> page = new PageImpl<>(userResponses);
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

    // Arrange
    when(usersService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable))
        .thenReturn(page);

    var result = mockMvcTester.get()
        .uri(ENDPOINT)
        .exchange();

    // Assert
    assertThat(result)
        .hasStatusOk()
        .bodyJson().satisfies(json -> {
          assertThat(json).extractingPath("$.content.length()").isEqualTo(userResponses.size());
          assertThat(json).extractingPath("$.content[0]")
              .convertTo(UserResponse.class).isEqualTo(userResponse);
        });


    verify(usersService, times(1))
        .findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
  }

  @Test
  void findById() {

    Long id = userResponse.getId();
    when(usersService.findById(anyLong())).thenReturn(userInfoResponse);


    var result = mockMvcTester.get()
        .uri(ENDPOINT + "/" + id.toString())
        .exchange();


    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(UserResponse.class)
        .isEqualTo(userResponse);

    verify(usersService, only()).findById(anyLong());

  }

  @Test
  void findById_NotFound() {
    Long id = userResponse.getId();
    when(usersService.findById(anyLong())).thenThrow(new UserNotFound("No existe el usuario"));

    var result = mockMvcTester.get()
        .uri(ENDPOINT + "/" + id.toString())
        .exchange();

    // Assert
    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND)
        .hasFailed().failure()
        .isInstanceOf(UserNotFound.class)
        .hasMessageContaining("No existe el usuario");

    verify(usersService, only()).findById(anyLong());
  }

  @Test
  void createUser() {
    String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "test1234"
           }
          """;

    when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);

    var result = mockMvcTester.post()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.CREATED)
        .bodyJson()
        .convertTo(UserResponse.class)
        .isEqualTo(userResponse);

    verify(usersService, only()).save(any(UserRequest.class));
  }

  @Test
  void createUserBadRequestPasswordMenosDe5Caracteres() {
    // Arrange
    String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "1234"
           }
          """;
    when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);

    var result = mockMvcTester.post()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .hasPathSatisfying("$.errors", path -> {
          assertThat(path).hasFieldOrProperty("password");
        });

    verify(usersService, never()).save(any(UserRequest.class));
  }

  @Test
  void createUser_BadRequestNombreApellidosEmailTodoEnBlanco() {
    String requestBody = """
          {
           "nombre": "",
           "apellidos": "",
           "username": "test",
           "email": "",
           "password": "test1234"
           }
          """;
    when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);

    var result = mockMvcTester.post()
        .uri(ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .hasPathSatisfying("$.errors", path -> {
          assertThat(path).hasFieldOrProperty("nombre");
          assertThat(path).hasFieldOrProperty("apellidos");
          assertThat(path).hasFieldOrProperty("email");
        });

    verify(usersService, never()).save(any(UserRequest.class));
  }

  @Test
  void updateUser() {
    Long id = userResponse.getId();
    String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "test1234"
           }
          """;
    when(usersService.update(anyLong(), any(UserRequest.class))).thenReturn(userResponse);

    var result = mockMvcTester.put()
        .uri(ENDPOINT+ "/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(UserResponse.class)
        .isEqualTo(userResponse);

    verify(usersService, only()).update(anyLong(), any(UserRequest.class));
  }

  @Test
  void updateUser_NotFound() {
    Long id = userResponse.getId();
    String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "test1234"
           }
          """;

    when(usersService.update(anyLong(), any(UserRequest.class)))
        .thenThrow(new UserNotFound("No existe el usuario"));

    var result = mockMvcTester.put()
        .uri(ENDPOINT+ "/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.NOT_FOUND)
        .hasFailed().failure()
        .isInstanceOf(UserNotFound.class)
        .hasMessageContaining("No existe el usuario");

    verify(usersService, only()).update(anyLong(), any(UserRequest.class));
  }

  @Test
  void deleteUser() {
    Long id = userResponse.getId();
    doNothing().when(usersService).deleteById(anyLong());

    var result = mockMvcTester.delete()
        .uri(ENDPOINT+ "/" + id)
        .exchange();
    assertThat(result)
        .hasStatus(HttpStatus.NO_CONTENT);

    verify(usersService, times(1)).deleteById(anyLong());
  }

  @Test
  void deleteUser_NotFound() {
    Long id = userResponse.getId();
    doThrow(new UserNotFound("No existe el usuario")).when(usersService).deleteById(anyLong());
    var result = mockMvcTester.delete()
        .uri(ENDPOINT+ "/" + id)
        .exchange();

    assertThat(result)
    .hasStatus(HttpStatus.NOT_FOUND)
        .hasFailed().failure()
        .isInstanceOf(UserNotFound.class)
        .hasMessageContaining("No existe el usuario");

    verify(usersService, only()).deleteById(anyLong());
  }

  @Test
  @WithUserDetails("admin")
  void me() {
    when(usersService.findById(anyLong())).thenReturn(userInfoResponse);

    var result = mockMvcTester.get()
        .uri(ENDPOINT+ "/me/profile")
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(UserResponse.class)
        .isEqualTo(userResponse);

    verify(usersService, only()).findById(anyLong());
  }

  @Test
  @WithAnonymousUser
  void me_AnonymousUser() {
    var result = mockMvcTester.get()
        .uri(ENDPOINT + "/me/profile")
        .exchange();

    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }

}