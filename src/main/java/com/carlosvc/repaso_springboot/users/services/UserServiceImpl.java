package com.carlosvc.repaso_springboot.users.services;

import com.carlosvc.repaso_springboot.Albumes.repository.AlbumRepository;
import com.carlosvc.repaso_springboot.users.dto.UserInfoResponse;
import com.carlosvc.repaso_springboot.users.dto.UserRequest;
import com.carlosvc.repaso_springboot.users.dto.UserResponse;
import com.carlosvc.repaso_springboot.users.exceptions.UserNameOrEmailExists;
import com.carlosvc.repaso_springboot.users.exceptions.UserNotFound;
import com.carlosvc.repaso_springboot.users.mappers.UserMapper;
import com.carlosvc.repaso_springboot.users.models.User;
import com.carlosvc.repaso_springboot.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserServiceImpl implements  UsersService {
    private final UserRepository userRepository;
    private final UserMapper usersMapper;
    private final AlbumRepository albumRepository;

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable){
        log.info("Buscando todos los usuarios con username: {} y borrados: {}", username, isDeleted);
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specEmail = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"),m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));


        Specification<User> criterio = Specification.allOf(
                specUsernameUser,
                specEmail,
                specIsDeleted

        );

        return userRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);


    }

    @Override
    @CachePut(key = "#id")
    public UserInfoResponse findById(Long id){
        log.info("Buscando usuario por id: {}", id);
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        var albumes = albumRepository.findByUsuarioId(id).stream().map((al -> al.getNombre())).toList();
        return usersMapper.toUserInfoResponse(user, albumes);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest){
        log.info("Guardando usuario: {}", userRequest);
        userRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                });
        return usersMapper.toUserResponse(userRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario: {}", userRequest);
        userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        userRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("usuario encontrado: " + u.getId() + " Mi id: " + id);
                        throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                    }
                });
        return usersMapper.toUserResponse(userRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        if (albumRepository.existsByUsuarioId(id)) {
            log.info("Borrado de usuario con id: {} ", id);
            userRepository.updateIsDeletedToTrueById(id);
        } else {
            log.info("Borrado total de usuario por id: {}", id);
            userRepository.delete(user);
        }
    }


    public List<User> findAllActiveUser(){
        log.info("Buscando todos los usuarios activos");
        return userRepository.findAllByIsDeletedFalse();

    }


}
