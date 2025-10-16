package com.carlosvc.repaso_springboot.Albumes.controllers;

import com.carlosvc.repaso_springboot.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumesCreateDto;
import com.carlosvc.repaso_springboot.Albumes.dto.AlbumesResponseDto;
import com.carlosvc.repaso_springboot.Albumes.models.Album;
import com.carlosvc.repaso_springboot.Albumes.services.AlbumesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/${API_VERSION:v1}/Albumes")
public class AlbumesRestController {
    private final AlbumesService albumesService;

    @Autowired
    public AlbumesRestController(AlbumesService albumesService) {
        this.albumesService = albumesService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumesResponseDto>> getAll(@RequestParam(required = false) String nombre,
                                                           @RequestParam(required = false) String banda) {
        log.info("Buscando tarjetas por numero={}, titular={}", nombre, banda);
        return ResponseEntity.ok(albumesService.findAll(nombre, banda));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumesResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id={}", id);
        return ResponseEntity.ok(albumesService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<AlbumesResponseDto> create(@RequestBody AlbumesCreateDto albumesCreateDto) {
        var saved = albumesService.save(albumesCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumesResponseDto> update(@PathVariable Long id, @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando album id={} con album={}", id, albumUpdateDto);
        return ResponseEntity.ok(albumesService.update(id,albumUpdateDto ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumesResponseDto> updatePartial(@PathVariable Long id, @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando parcialmente album con id={} con album={}",id, albumUpdateDto);
        return ResponseEntity.ok(albumesService.update(id, albumUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando producto por id: " + id);
        albumesService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
