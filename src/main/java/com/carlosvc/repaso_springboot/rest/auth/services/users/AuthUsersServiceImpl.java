package com.carlosvc.repaso_springboot.rest.auth.services.users;

import com.carlosvc.repaso_springboot.rest.auth.repositories.AuthUsersRepository;
import com.carlosvc.repaso_springboot.rest.users.exceptions.UserNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("userDetailsService")
public class AuthUsersServiceImpl implements AuthUsersService {

    private final AuthUsersRepository authUsersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("Usuario con username " + username + " no encontrado"));
    }
}
