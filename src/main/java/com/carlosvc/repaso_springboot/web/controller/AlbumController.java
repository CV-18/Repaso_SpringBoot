package com.carlosvc.repaso_springboot.web.controller;

import com.carlosvc.repaso_springboot.rest.Albumes.models.Album;
import com.carlosvc.repaso_springboot.rest.Albumes.services.AlbumesService;
import com.carlosvc.repaso_springboot.rest.users.models.User;
import com.carlosvc.repaso_springboot.rest.users.services.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class AlbumController {
    private final AlbumesService albumesService;
    private final UsersService usersService;

    @GetMapping("/allalbumes")
    public String allAlbumes(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> usuario = usersService.findByUsername(username);
        List<Album> albumes = List.of();
        if (usuario.isPresent()){
          albumes = albumesService.buscarPorUsuarioId(usuario.get().getId());
        }

        model.addAttribute("albumes", albumes);
        return "app/albumes/lista";
    }

    @GetMapping("/allalbumes/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Album album = albumesService.buscarPorId(id).orElse(null);
        model.addAttribute("album", album);
        return "app/albumes/detalle";
    }
}
