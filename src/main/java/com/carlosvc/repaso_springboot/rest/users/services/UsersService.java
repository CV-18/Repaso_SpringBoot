package com.carlosvc.repaso_springboot.rest.users.services;

import com.carlosvc.repaso_springboot.rest.users.dto.UserInfoResponse;
import com.carlosvc.repaso_springboot.rest.users.dto.UserRequest;
import com.carlosvc.repaso_springboot.rest.users.dto.UserResponse;
import com.carlosvc.repaso_springboot.rest.users.models.User;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable);

    UserInfoResponse findById(Long id);

    UserResponse save(UserRequest userRequest);

    UserResponse update(Long id, UserRequest userRequest);

    void deleteById(Long id);

    List<User> findAllActiveUser();

    Optional<User> findByUsername(String username);
    void save(User user);
}
