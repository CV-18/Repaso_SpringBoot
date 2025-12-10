package com.carlosvc.repaso_springboot.Albumes.controllers;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.utils.PageResponse;
import com.carlosvc.repaso_springboot.utils.PaginationLinksUtils;
import jakarta.servlet.ServletResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.ldap.PagedResultsResponseControl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/Albumes")
public class AlbumesRestController {
    private final AlbumesService albumesService;
    private final PaginationLinksUtils paginationLinksUtils;
    

    @GetMapping
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAll(@RequestParam(required = false) Optional<String> nombre,
                                                                 @RequestParam(required = false)Optional<String> discografica,
                                                                 @RequestParam(required = false)Optional<Boolean> isDeleted,
                                                                 @RequestParam(defaultValue = "0")int page,
                                                                 @RequestParam(defaultValue = "10")int size,
                                                                 @RequestParam(defaultValue = "id")String sortBy,
                                                                 @RequestParam(defaultValue = "asc")String direction,
                                                                 HttpServletRequest request, ServletResponse servletResponse){

        log.info("Buscando tarjetas por numero= {}, discografica= {}, isDeleted= {}", nombre, discografica, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<AlbumResponseDto> pageResult = albumesService.findAll(nombre, discografica, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy,direction));
    }


    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id={}", id);
        return ResponseEntity.ok(albumesService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        log.info("Creando album {}", albumCreateDto);
        var saved = albumesService.save(albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id,@Valid  @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando album id={} con album={}", id, albumUpdateDto);
        return ResponseEntity.ok(albumesService.update(id, albumUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id,@Valid  @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando parcialmente album con id={} con album={}",id, albumUpdateDto);
        return ResponseEntity.ok(albumesService.update(id, albumUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando producto por id: " + id);
        albumesService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validacion para el objeto='" + result.getObjectName()
        + "'. " + "Núm. errores: " + result.getErrorCount());

        Map<String, String> errors = new HashMap<>();
        result.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errors",errors);
        return problemDetail;
    }

}
