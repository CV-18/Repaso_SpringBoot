package com.carlosvc.repaso_springboot.web.controller;

import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/public")
public class ZonaPublicaController {
    private final AlbumesService  albumesService;



}
