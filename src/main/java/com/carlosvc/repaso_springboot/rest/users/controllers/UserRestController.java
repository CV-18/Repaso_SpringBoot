package com.carlosvc.repaso_springboot.rest.users.controllers;


import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.rest.users.dto.UserInfoResponse;
import com.carlosvc.repaso_springboot.rest.users.dto.UserRequest;
import com.carlosvc.repaso_springboot.rest.users.dto.UserResponse;
import com.carlosvc.repaso_springboot.rest.users.models.User;
import com.carlosvc.repaso_springboot.rest.users.services.UsersService;
import com.carlosvc.repaso_springboot.utils.PageResponse;
import com.carlosvc.repaso_springboot.utils.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/users")
@PreAuthorize("hasRole('USER')")
public class UserRestController {
    private final UsersService userService;
    private final PaginationLinksUtils paginationLinksUtils;
    private final AlbumesService albumesService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false)Optional<String> username,
            @RequestParam(required = false)Optional<String> email,
            @RequestParam(required = false)Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
            ){
        log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",username,email,isDeleted,page,size,sortBy,direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<UserResponse> pageResult = userService.findAll(username,email,isDeleted,org.springframework.data.domain.PageRequest.of(page,size,sort));

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriComponentsBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));


    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id){
        log.info("findById: id: {}",id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest){
        log.info("createUser: userRequest: {}",userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userRequest));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("updateUser: id: {}, userRequest: {}", id, userRequest);
        return ResponseEntity.ok(userService.update(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("deleteUser: id: {}", id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user){
        log.info("Obteniendo usuario");
        return ResponseEntity.ok(userService.findById(user.getId()));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user,
                                                     @Valid @RequestBody UserRequest userRequest){
        log.info("updateMe: user: {}, userRequest: {}", user,userRequest);
        return ResponseEntity.ok(userService.update(user.getId(), userRequest));
    }


    @DeleteMapping("me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user){
        log.info("deleteMe: user: {}", user);
        userService.deleteById(user.getId());
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/me/albumes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAlbumesByUsuario(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction

    ){
        log.info("Obteniendo albumes del usuario: {}", user);
        Sort sort = direction.equalsIgnoreCase(
                Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        return ResponseEntity.ok(PageResponse.of(
                albumesService.findByUsuarioId(user.getId(), pageable),
                sortBy,
                direction
        ));

    }

    @GetMapping("/me/Albumes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlbumResponseDto> getAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlbum
    ){
        log.info("Obteniendo album con id: {}",idAlbum);
        return  ResponseEntity.ok(albumesService.findByUsuarioId(user.getId(),idAlbum));
    }


    @PostMapping("/me/Albumes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlbumResponseDto> createAlbum(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AlbumCreateDto albumCreateDto

    ){
        log.info("Creando album: {}", albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumesService.save(albumCreateDto, user.getId()));
    }


    @PutMapping("/me/Albumes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlbumResponseDto>updateAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlbum,
            @Valid @RequestBody AlbumUpdateDto albumUpdateDto
    ){
        log.info("Actualizado album con id: {}", idAlbum);
        return ResponseEntity.ok(albumesService.update(idAlbum, albumUpdateDto, user.getId()));

    }

    @DeleteMapping("/me/Albumes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlbum

    ){
        log.info("Borrando album con id: {}", idAlbum);
        albumesService.deleteById(idAlbum, user.getId());
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

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
