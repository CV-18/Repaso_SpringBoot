package com.carlosvc.repaso_springboot.rest.auth.controllers;

import com.carlosvc.repaso_springboot.rest.auth.dto.JwtAuthResponse;
import com.carlosvc.repaso_springboot.rest.auth.dto.UserSignInRequest;
import com.carlosvc.repaso_springboot.rest.auth.dto.UserSignUpRequest;
import com.carlosvc.repaso_springboot.rest.auth.exceptions.AuthDifferentPasswords;
import com.carlosvc.repaso_springboot.rest.auth.exceptions.AuthExistingUsernameOrEmail;
import com.carlosvc.repaso_springboot.rest.auth.exceptions.AuthSignInNotValid;
import com.carlosvc.repaso_springboot.rest.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerTest {

  private final String ENDPOINT = "/api/v1/auth";

  @Autowired
  private MockMvcTester mockMvcTester;

  @MockitoBean
  private AuthenticationService authenticationService;

  @Test
  void signUp() {
    String requestBody = """
          {
           "nombre": "Test",
           "apellidos": "Test",
           "username": "test2",
           "email": "test@test.com",
           "password": "12345",
           "passwordComprobacion": "12345"
           }
          """;

    var jwtAuthResponse = JwtAuthResponse.builder().token("token").build();
    when(authenticationService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(JwtAuthResponse.class)
        .isEqualTo(jwtAuthResponse);

    verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
  }

  @Test
  void signUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
    String requestBody = """
          {
           "nombre": "Test",
           "apellidos": "Test",
           "username": "test2",
           "email": "test@test.com",
           "password": "12345",
           "passwordComprobacion": "54321"
           }
          """;

    when(authenticationService.signUp(any(UserSignUpRequest.class)))
        .thenThrow(new AuthDifferentPasswords("Las contraseñas no coinciden"));

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasFailed().failure()
        .isInstanceOf(AuthDifferentPasswords.class)
        .hasMessageContaining("no coinciden");

    verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
  }

  @Test
  void signUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
    String requestBody = """
          {
           "nombre": "Test",
           "apellidos": "Test",
           "username": "test",
           "email": "test@test.com",
           "password": "12345",
           "passwordComprobacion": "12345"
           }
          """;

    when(authenticationService.signUp(any(UserSignUpRequest.class)))
        .thenThrow(new AuthExistingUsernameOrEmail("El usuario con username XXX o email XXX ya existe"));

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasFailed().failure()
        .isInstanceOf(AuthExistingUsernameOrEmail.class)
        .hasMessageContaining("ya existe");

    verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));

  }

  @Test
  void signUp_BadRequest_When_Nombre_Apellidos_Email_Username_Empty_ShouldThrowException() {
    String requestBody = """
          {
           "nombre": "",
           "apellidos": "",
           "username": "",
           "email": "",
           "password": "12345",
           "passwordComprobacion": "12345"
           }
          """;

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .hasPathSatisfying("$.errors", path -> {
          assertThat(path).hasFieldOrProperty("nombre");
          assertThat(path).hasFieldOrProperty("apellidos");
          assertThat(path).hasFieldOrProperty("username");
          assertThat(path).hasFieldOrProperty("email");
        });
    verify(authenticationService, never()).signUp(any(UserSignUpRequest.class));

  }

  @Test
  void signIn() {
    String requestBody = """
          {
           "username": "test2",
           "password": "12345"
           }
          """;

    var jwtAuthResponse = JwtAuthResponse.builder().token("token").build();
    when(authenticationService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .convertTo(JwtAuthResponse.class)
        .isEqualTo(jwtAuthResponse);

    verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));

  }

  @Test
  void signIn_NotValid() {
    String requestBody = """
          {
           "username": "test2",
           "password": "password"
           }
          """;

    when(authenticationService.signIn(any(UserSignInRequest.class)))
        .thenThrow(new AuthSignInNotValid("Usuario o contraseña incorrectos"));

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus4xxClientError()
        .hasFailed().failure()
        .isInstanceOf(AuthSignInNotValid.class)
        .hasMessageContaining("incorrectos");

    verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));

  }

  @Test
  void signIn_BadRequest_When_Username_Password_Empty_ShouldThrowException() {
    String requestBody = """
          {
           "username": "",
           "password": ""
           }
          """;

    var result = mockMvcTester.post()
        .uri(ENDPOINT + "/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .exchange();

    assertThat(result)
        .hasStatus(HttpStatus.BAD_REQUEST)
        .bodyJson()
        .hasPathSatisfying("$.errors", path -> {
          assertThat(path).hasFieldOrProperty("username");
          assertThat(path).hasFieldOrProperty("password");
        });

    verify(authenticationService, never()).signIn(any(UserSignInRequest.class));
  }

}
