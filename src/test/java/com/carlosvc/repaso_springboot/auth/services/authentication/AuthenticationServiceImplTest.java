package com.carlosvc.repaso_springboot.auth.services.authentication;

import com.carlosvc.repaso_springboot.auth.dto.JwtAuthResponse;
import com.carlosvc.repaso_springboot.auth.dto.UserSignInRequest;
import com.carlosvc.repaso_springboot.auth.dto.UserSignUpRequest;
import com.carlosvc.repaso_springboot.auth.exceptions.AuthDifferentPasswords;
import com.carlosvc.repaso_springboot.auth.exceptions.AuthExistingUsernameOrEmail;
import com.carlosvc.repaso_springboot.auth.exceptions.AuthSignInNotValid;
import com.carlosvc.repaso_springboot.auth.repositories.AuthUsersRepository;
import com.carlosvc.repaso_springboot.auth.services.jwt.JwtService;
import com.carlosvc.repaso_springboot.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Mock
  private AuthUsersRepository authUsersRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Test
  public void testSignUp_WhenPasswordsMatch_ShouldReturnToken() {
    UserSignUpRequest request = UserSignUpRequest.builder()
        .nombre("Test")
        .apellidos("User")
        .username("testuser")
        .email("test@example.com")
        .password("password")
        .passwordComprobacion("password")
        .build();

    User userStored = new User();
    when(authUsersRepository.save(any(User.class))).thenReturn(userStored);

    String token = "test_token";
    when(jwtService.generateToken(userStored)).thenReturn(token);

    JwtAuthResponse response = authenticationService.signUp(request);

    assertAll("Sign Up",
        () -> assertNotNull(response),
        () -> assertEquals(token, response.getToken()),
        () -> verify(authUsersRepository, times(1)).save(any(User.class)),
        () -> verify(jwtService, times(1)).generateToken(userStored)
    );
  }

  @Test
  public void testSignUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
    UserSignUpRequest request = UserSignUpRequest.builder()
        .nombre("Test")
        .apellidos("User")
        .username("testuser")
        .email("test@example.com")
        .password("password1")
        .passwordComprobacion("password2")
        .build();

    assertThrows(AuthDifferentPasswords.class, () -> authenticationService.signUp(request));
  }

  @Test
  public void testSignUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
    UserSignUpRequest request = UserSignUpRequest.builder()
        .nombre("Test")
        .apellidos("User")
        .username("testuser")
        .email("test@example.com")
        .password("password")
        .passwordComprobacion("password")
        .build();

    when(authUsersRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

    assertThrows(AuthExistingUsernameOrEmail.class, () -> authenticationService.signUp(request));
  }

  @Test
  public void testSignIn_WhenValidCredentials_ShouldReturnToken() {
    UserSignInRequest request = UserSignInRequest.builder()
        .username("testuser")
        .password("password")
        .build();

    User user = new User();
    when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

    String token = "test_token";
    when(jwtService.generateToken(user)).thenReturn(token);

    JwtAuthResponse response = authenticationService.signIn(request);

    assertAll("Sign In",
        () -> assertNotNull(response),
        () -> assertEquals(token, response.getToken()),
        () -> verify(authenticationManager, times(1))
            .authenticate(any(UsernamePasswordAuthenticationToken.class)),
        () -> verify(authUsersRepository, times(1)).findByUsername(request.getUsername()),
        () -> verify(jwtService, times(1)).generateToken(user)
    );
  }

  @Test
  public void testSignIn_WhenInvalidCredentials_ShouldThrowException() {
    UserSignInRequest request = UserSignInRequest.builder()
        .username("testuser")
        .password("password")
        .build();

    when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

    assertThrows(AuthSignInNotValid.class, () -> authenticationService.signIn(request));
  }

}