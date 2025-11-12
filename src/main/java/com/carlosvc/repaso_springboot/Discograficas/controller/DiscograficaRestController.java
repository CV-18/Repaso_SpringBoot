package com.carlosvc.repaso_springboot.Discograficas.controller;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.services.DiscograficasService;
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
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/Discograficas")
public class DiscograficaRestController {
    private final DiscograficasService discoService;

    @GetMapping()
    public ResponseEntity<List<Discografica>> getAll(@RequestParam(required = false) String nombre) {
        log.debug("REST request to get all Discograficas");
        return ResponseEntity.ok(discoService.findAll(nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discografica> getById(@PathVariable Long id) {
        log.debug("REST request to get Discografica : {}", id);
        return ResponseEntity.ok(discoService.findById(id));
    }


    @PostMapping
    public ResponseEntity<Discografica> create(@Valid @RequestBody DiscograficaRequestDTO discograficaRequestDTO) {
        log.info("Creando discografica: {}", discograficaRequestDTO);
        var saved =  discoService.save(discograficaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discografica> update(@Valid @RequestBody Long id, DiscograficaRequestDTO discograficaRequestDTO) {
        log.debug("REST request to update Discografica : {}", id);
        return ResponseEntity.ok(discoService.update(id, discograficaRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Discografica : {}", id);
        discoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult bindingResult = ex.getBindingResult();

        problemDetail.setDetail("Falló la validacion para el objeto='" + bindingResult.getObjectName()
        + "'. " + "Num. errores: " + bindingResult.getErrorCount());

        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            String message = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(errorMessage, message);
        });

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }


}
