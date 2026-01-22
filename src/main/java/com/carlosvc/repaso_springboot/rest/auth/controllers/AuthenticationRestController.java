package com.carlosvc.repaso_springboot.rest.auth.controllers;


import com.carlosvc.repaso_springboot.rest.auth.dto.JwtAuthResponse;
import com.carlosvc.repaso_springboot.rest.auth.dto.UserSignInRequest;
import com.carlosvc.repaso_springboot.rest.auth.dto.UserSignUpRequest;
import com.carlosvc.repaso_springboot.rest.auth.services.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/auth")

public class AuthenticationRestController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponse> singnUp(@Valid @RequestBody UserSignUpRequest request){
        log.info("Creando usuario: {}", request);
        return ResponseEntity.ok(authenticationService.signUp(request));

    }

    @PostMapping("/signin")
    public  ResponseEntity<JwtAuthResponse> signIn(@Valid @RequestBody UserSignInRequest request){
        log.info("Iniciando sesión de usuario: {}",request);
        return ResponseEntity.ok(authenticationService.signIn(request));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errors: " + result.getErrorCount());

        Map<String, String> errors = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}
