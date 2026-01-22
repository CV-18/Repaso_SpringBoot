package com.carlosvc.repaso_springboot.web.controller;


import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("albums")
public class AlbumController {
    private final AlbumesService albumesService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        AlbumResponseDto albumResponseDto = albumesService.findById(id);
        model.addAttribute("album", albumResponseDto);
        return "album/detalle";
    }

    @GetMapping({"", "/", "/lista"})
    public String lista(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AlbumResponseDto> albumesPage = albumesService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);
        model.addAttribute("albums", albumesPage);
        return "album/lista";
    }

    @GetMapping("/new")
    public String nuevaAlbumSubmit(@Valid @ModelAttribute("album")AlbumCreateDto albumCreateDto, BindingResult bindingResult) {
        log.info("Datos recibidos del formulario: {}", albumCreateDto);
        if (bindingResult.hasErrors()) {
            log.info("hay errores en la validación");
            return "/album/form";
        }else {
            albumesService.save(albumCreateDto);
            return "redirect:/albums/lista";
        }
    }


    @GetMapping("/{id}/edit")
    public String editarAlbumForm(@PathVariable Long id, Model model){
        AlbumResponseDto albumResponseDto = albumesService.findById(id);
        if (albumResponseDto != null) {
            return "redirect:/albums/new";
        } else {
            AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
                    .nombre(albumResponseDto.getNombre())
                    .banda(albumResponseDto.getBanda())
                    .anio(albumResponseDto.getAnio())
                    .genero(albumResponseDto.getGenero())
                    .precio(albumResponseDto.getPrecio())
                    .build();
            model.addAttribute("album", albumResponseDto);
            model.addAttribute("albumId", id);
            model.addAttribute("modoEditar", true);
            return "album/form";
        }
    }

    @PostMapping("/{id}/edit")
    public String editarAlbumesSubmit(@PathVariable("id")Long id,
                                      @Valid @ModelAttribute("album") AlbumUpdateDto albumUpdateDto,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    "Ha ocurrido un error al actualizar el album");
            model.addAttribute("album", id);
            model.addAttribute("modoEditar", true);
            return "/album/form";
        }
        albumesService.update(id, albumUpdateDto);
        redirectAttributes.addFlashAttribute("message",
                "Album actualizada correctamente");
        return "redirect:/albums/{id}";
    }

    @GetMapping("/{id}/delete")
    public String borrarAlbum(@PathVariable Long id) {
        albumesService.deleteById(id);
        return "redirect:/albums/lista";
    }
}
