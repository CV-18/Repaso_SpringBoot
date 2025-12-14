package com.carlosvc.repaso_springboot.auth.services.authentication;

import com.carlosvc.repaso_springboot.auth.dto.JwtAuthResponse;
import com.carlosvc.repaso_springboot.auth.dto.UserSignInRequest;
import com.carlosvc.repaso_springboot.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);
    JwtAuthResponse signIn(UserSignInRequest request);

}
