package com.carlosvc.repaso_springboot.web.controller;

import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumCreateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumUpdateDto;
import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.web.services.I18nService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final AlbumesService albumesService;
  private final I18nService i18nService;

  @GetMapping("/albumes")
  public String albumes(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<AlbumResponseDto> albumResponseDtos = albumesService.findAll(
      Optional.empty(), Optional.empty(), Optional.empty(), pageable
    );
    model.addAttribute("page", albumResponseDtos);
    return "admin/albumes/lista";
  }

  @GetMapping("/albumes/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Album album = albumesService.buscarPorId(id).orElse(null);
    model.addAttribute("album", album);
    return "admin/albumes/detalle";
  }

  @GetMapping("/albumes/new")
  public String nuevoAlbumForm(Model model){
    model.addAttribute("album", AlbumCreateDto.builder().build());
    model.addAttribute("modoEditar", false);
    return "admin/albumes/form";
  }

  @PostMapping("/albumes/new")
  public String nuevoAlbumSubmit(@Valid @ModelAttribute("album") AlbumCreateDto albumCreateDto, BindingResult bindingResult){
    if (bindingResult.hasErrors()){
      return "admin/albumes/form";
    } else {
      albumesService.save(albumCreateDto);
      return "redirect:/admin/albumes";
    }
  }

  @GetMapping("/albumes/{id}/edit")
  public String editarAlbumForm(@PathVariable Long id, Model model){
    Album album = albumesService.buscarPorId(id).orElse(null);
    if(album == null){
      return "redirect:/admin/albumes/new";
    } else {
      AlbumUpdateDto albumUpdateDto = AlbumUpdateDto.builder()
        .nombre(album.getNombre())
        .anio(album.getAnio())
        .banda(album.getBanda())
        .genero(album.getGenero())
        .precio(album.getPrecio())
        .discografica(album.getDiscografica().getNombre())
        .build();
      model.addAttribute("album", albumUpdateDto);
      model.addAttribute("albumId", id);
      model.addAttribute("modoEditar", true);
      return "admin/albumes/form";
    }
  }

  @PostMapping("/albumes/{id}/edit")
  public String editarAlbumSubmit(@PathVariable("id") Long id, @Valid @ModelAttribute("album") AlbumUpdateDto albumUpdateDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes){
    if (bindingResult.hasErrors()){
      model.addAttribute("albumId", id);
      model.addAttribute("modoEditar", true);
      return "admin/albumes/form";
    }
    albumesService.update(id, albumUpdateDto);
    return "redirect:/admin/albumes/" + id;
  }

  @GetMapping("/albumes/{id}/delete")
  public String borrarAlbum(@PathVariable Long id){
    albumesService.deleteById(id);
    return "redirect:/admin/albumes";
  }
}
