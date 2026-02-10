package com.carlosvc.repaso_springboot.web.controller;


import ch.qos.logback.core.model.Model;
import com.carlosvc.repaso_springboot.rest.Albumes.dto.AlbumResponseDto;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.web.services.I18nService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    Page<AlbumResponseDto> albumResponseDtos
  }
}
