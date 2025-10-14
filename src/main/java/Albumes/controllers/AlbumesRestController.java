package Albumes.controllers;

import Albumes.models.Album;
import Albumes.services.AlbumesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/${api.version}/Albumes")
public class AlbumesRestController {
    private final AlbumesService albumesService;

    @Autowired
    public AlbumesRestController(AlbumesService albumesService) {
        this.albumesService = albumesService;
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAll(@RequestParam(required = false) String nombre,
                                              @RequestParam(required = false) String banda) {
        log.info("Buscando tarjetas por numero={}, titular={}", nombre, banda);
        return ResponseEntity.ok(albumesService.findAll(nombre, banda));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getById(@PathVariable Long id) {
        log.info("Buscando album por id={}", id);
        return ResponseEntity.ok(albumesService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Album> create(@RequestBody Album album) {
        var saved = albumesService.save(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> update(@PathVariable Long id, @RequestBody Album album) {
        log.info("Actualizando album id={} con album={}", id, album);
        return ResponseEntity.ok(albumesService.update(id, album));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Album> updatePartial(@PathVariable Long id, @RequestBody Album album) {
        log.info("Actualizando parcialmente album con id={} con album={}",id, album);
        return ResponseEntity.ok(albumesService.update(id, album));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando producto por id: " + id);
        albumesService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
