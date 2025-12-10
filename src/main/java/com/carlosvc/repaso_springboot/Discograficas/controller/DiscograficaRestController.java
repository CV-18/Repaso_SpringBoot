package com.carlosvc.repaso_springboot.Discograficas.controller;

import com.carlosvc.repaso_springboot.Discograficas.dto.DiscograficaRequestDTO;
import com.carlosvc.repaso_springboot.Discograficas.models.Discografica;
import com.carlosvc.repaso_springboot.Discograficas.services.DiscograficasService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/Discograficas")
public class DiscograficaRestController {
    private final DiscograficasService discoService;
    private final PaginationLinksUtils  paginationLinksUtils;

    @GetMapping()
    public ResponseEntity<PageResponse<Discografica>> getAll(@RequestParam(required = false) Optional<String> nombre,
                                                 @RequestParam(required = false) Optional<Boolean> isDeleted,
                                                 @RequestParam(defaultValue = "0")int page,
                                                 @RequestParam(defaultValue = "10")int size,
                                                 @RequestParam(defaultValue = "id")String sortBy,
                                                 @RequestParam(defaultValue = "asc")String direction,
                                                 HttpServletRequest  request
                                                     ) {
        log.debug("Buscando todos las discograficas con nombre {}, isDeleted:{}", nombre,isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<Discografica> pageDiscograficas = discoService.findAll(nombre, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link",paginationLinksUtils.createLinkHeader(pageDiscograficas,uriComponentsBuilder))
                .body(PageResponse.of(pageDiscograficas,sortBy,direction));
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
    public ResponseEntity<Discografica> update(@PathVariable Long id, @Valid @RequestBody DiscograficaRequestDTO discograficaRequestDTO) {
        log.info("Actualizando discografica por id: {}", id);
        return ResponseEntity.ok(discoService.update(id, discograficaRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando discografica por id: {}", id);
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
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }


}
