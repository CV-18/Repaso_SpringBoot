package com.carlosvc.repaso_springboot.users.services;

import com.carlosvc.repaso_springboot.rest.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.rest.users.dto.UserInfoResponse;
import com.carlosvc.repaso_springboot.rest.users.dto.UserRequest;
import com.carlosvc.repaso_springboot.rest.users.dto.UserResponse;
import com.carlosvc.repaso_springboot.rest.users.exceptions.UserNameOrEmailExists;
import com.carlosvc.repaso_springboot.rest.users.exceptions.UserNotFound;
import com.carlosvc.repaso_springboot.rest.users.mappers.UserMapper;
import com.carlosvc.repaso_springboot.rest.users.models.User;
import com.carlosvc.repaso_springboot.rest.users.repositories.UserRepository;
import com.carlosvc.repaso_springboot.rest.users.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {
  private final UserRequest userRequest = UserRequest.builder()
      .username("test").email("test@test.com").build();
  private final User user = User.builder()
      .id(99L).username("test").email("test@test.com").build();

  @Mock
  private UserRepository usersRepository;
  @Mock
  private AlbumRepository albumRepository;
  @Spy
  private UserMapper usersMapper;
  @InjectMocks
  private UserServiceImpl usersService;

  @Test
  public void testFindAll_NoFilters_ReturnsPageOfUsers() {
    List<User> users = Arrays.asList(new User(), new User());
    Page<User> page = new PageImpl<>(users);
    when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    Page<UserResponse> result = usersService.findAll(
        Optional.empty(), Optional.empty(), Optional.empty(), Pageable.unpaged());

    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(2, result.getTotalElements())
    );

    verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  public void testFindById() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
    when(albumRepository.findByUsuarioId(userId)).thenReturn(List.of());

    UserInfoResponse result = usersService.findById(userId);

    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(userRequest.getUsername(), result.getUsername()),
        () -> assertEquals(userRequest.getEmail(), result.getEmail())
    );

    verify(usersRepository, times(1)).findById(userId);
    verify(albumRepository, times(1)).findByUsuarioId(userId);

  }

  @Test
  public void testFindById_UserNotFound_ThrowsUserNotFound() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFound.class, () -> usersService.findById(userId));

    verify(usersRepository, times(1)).findById(userId);
  }

  @Test
  public void testSave_ValidUserRequest_ReturnsUserResponse() {
    when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
        anyString(), anyString())).thenReturn(Optional.empty());
    when(usersRepository.save(any(User.class))).thenReturn(user);

    UserResponse result = usersService.save(userRequest);

    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(userRequest.getUsername(), result.getUsername()),
        () -> assertEquals(userRequest.getEmail(), result.getEmail())
    );

    verify(usersRepository, times(1))
        .findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString());
    verify(usersRepository, times(1)).save(any(User.class));

  }

  @Test
  public void testSave_DuplicateUsernameOrEmail_ThrowsUserNameOrEmailExists() {
    when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
        anyString(), anyString())).thenReturn(Optional.of(user));

    assertThrows(UserNameOrEmailExists.class, () -> usersService.save(userRequest));
  }

  @Test
  public void testUpdate_ValidUserRequest_ReturnsUserResponse() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
    when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
        anyString(), anyString())).thenReturn(Optional.empty());
    when(usersRepository.save(any(User.class))).thenReturn(user);

    UserResponse result = usersService.update(userId, userRequest);

    assertAll(
        () -> assertNotNull(result),
        () -> assertEquals(userRequest.getUsername(), result.getUsername()),
        () -> assertEquals(userRequest.getEmail(), result.getEmail())
    );

    verify(usersRepository, times(1)).findById(userId);
    verify(usersRepository, times(1))
        .findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(anyString(), anyString());
    verify(usersRepository, times(1)).save(any(User.class));
  }

  @Test
  public void testUpdate_DuplicateUsernameOrEmail_ThrowsUserNameOrEmailExists() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
    when(usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(
        anyString(), anyString())).thenReturn(Optional.of(user));

    assertThrows(UserNameOrEmailExists.class, () -> usersService.update(userId, userRequest));
  }

  @Test
  public void testUpdate_UserNotFound_ThrowsUserNotFound() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFound.class, () -> usersService.update(userId, userRequest));
  }

  @Test
  public void testDeleteById_PhisicalDelete() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
    when(albumRepository.existsByUsuarioId(userId)).thenReturn(false);

    usersService.deleteById(userId);

    verify(usersRepository, times(1)).delete(user);
    verify(albumRepository, times(1)).existsByUsuarioId(userId);
  }

  @Test
  public void testDeleteById_LogicalDelete() {
    Long userId = 1L;
    when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
    when(albumRepository.existsByUsuarioId(userId)).thenReturn(true);
    doNothing().when(usersRepository).updateIsDeletedToTrueById(userId);

    usersService.deleteById(userId);

    verify(usersRepository, times(1)).updateIsDeletedToTrueById(userId);
    verify(albumRepository, times(1)).existsByUsuarioId(userId);
  }

  @Test
  public void testDeleteByIdNotExists() {
    Long userId = 1L;
    User user = new User();
    when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
    when(albumRepository.existsByUsuarioId(userId)).thenReturn(true);

    usersService.deleteById(userId);

    verify(usersRepository, times(1)).updateIsDeletedToTrueById(userId);
    verify(albumRepository, times(1)).existsByUsuarioId(userId);
  }

}