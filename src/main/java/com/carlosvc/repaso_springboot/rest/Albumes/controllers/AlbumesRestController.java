package com.carlosvc.repaso_springboot.rest.Albumes.controllers;

import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.utils.PageResponse;
import com.carlosvc.repaso_springboot.utils.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Parameters;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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


    @Operation(summary = "Obtiene todas los albumes", description = "Obtiene una lista de albumes")
    @Parameters({
            @Parameter(name = "nombre", description = "Nombre del album", example = ""),
            @Parameter(name = "discografica", description = "Discografica del album", example = ""),
            @Parameter(name = "isDeleted", description = "Si está borrada o no", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de albumes"),
    })
    

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


    @Operation(summary = "Obtiene una lista de albumes por el nombre de la banda", description ="Obtiene una lista de albumes por el nombre de la banda" )
    @Parameters({
            @Parameter(name = "banda",description = "Nombre del grupo", example = "Bathory", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Lista de albumes"),
            @ApiResponse(responseCode = "404",description = "Banda no encontrada")
    })
    @GetMapping("/banda/{banda}")
    public ResponseEntity<List<AlbumResponseDto>> getByBanda(@PathVariable String banda) {
        log.info("Buscando albumes por banda={}", banda);
        return ResponseEntity.ok(albumesService.findByBanda(banda));
    }


    @Operation(summary = "Obtiene un album por id", description ="Obtiene un album por id" )
    @Parameters({
            @Parameter(name = "id",description = "Identificador del album", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Albums"),
            @ApiResponse(responseCode = "404",description = "Album no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id={}", id);
        return ResponseEntity.ok(albumesService.findById(id));
    }

    @Operation(summary = "Crea un album", description = "Crea un album")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Album a crear", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album creado"),
            @ApiResponse(responseCode = "400", description = "Album no válido"),
    })
    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        log.info("Creando album {}", albumCreateDto);
        var saved = albumesService.save(albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }



    @Operation(summary = "Actualiza un album", description = "Actualiza un album")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Album a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album actualizado"),
            @ApiResponse(responseCode = "400", description = "Album no válido"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id,@Valid  @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando album id={} con album={}", id, albumUpdateDto);
        return ResponseEntity.ok(albumesService.update(id, albumUpdateDto));
    }


    @Operation(summary = "Actualiza parcialmente un album", description = "Actualiza parcialmente un album")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Album a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album actualizado"),
            @ApiResponse(responseCode = "400", description = "Album no válido"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id,@Valid  @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando parcialmente album con id={} con album={}",id, albumUpdateDto);
        return ResponseEntity.ok(albumesService.update(id, albumUpdateDto));
    }




    @Operation(summary = "Borra un album", description = "Borra un album")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Album borrado"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando album por id: " + id);
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
