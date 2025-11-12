package com.carlosvc.repaso_springboot.Albumes.controllers;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.services.AlbumesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/Albumes")
public class AlbumesRestController {
    private final AlbumesService albumesService;
    

    @GetMapping
    public ResponseEntity<List<AlbumResponseDto>> getAll(@RequestParam(required = false) String nombre,
                                                         @RequestParam(required = false) String cliente) {
        log.info("Buscando tarjetas por numero={}, titular={}", nombre, cliente);
        return ResponseEntity.ok(albumesService.findAll(nombre, cliente));
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
