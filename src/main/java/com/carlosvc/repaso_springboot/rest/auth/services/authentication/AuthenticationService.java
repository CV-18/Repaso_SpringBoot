package com.carlosvc.repaso_springboot.rest.auth.services.authentication;

import com.carlosvc.repaso_springboot.rest.auth.dto.JwtAuthResponse;
import com.carlosvc.repaso_springboot.rest.auth.dto.UserSignInRequest;
import com.carlosvc.repaso_springboot.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);
    JwtAuthResponse signIn(UserSignInRequest request);

}
