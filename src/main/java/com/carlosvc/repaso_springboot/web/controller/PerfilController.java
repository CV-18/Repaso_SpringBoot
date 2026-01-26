package com.carlosvc.repaso_springboot.web.controller;


import com.carlosvc.repaso_springboot.rest.users.models.User;
import com.carlosvc.repaso_springboot.rest.users.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/app/perfil")
public class PerfilController {
    private final UsersService usersService;

    @GetMapping
    public String showPerfil(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = usersService.findByUsername(username).orElse(null);
        model.addAttribute("usuario", user);
        return "app/perfil";
    }

    @PostMapping("/edit")
    public String editarPerfil(@Valid @ModelAttribute("usuario") User updateUser,
                               BindingResult bindingResult,
                               Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("mensaje", "Ha ocurrido un error al actualizar el perfil");
            return "redirect:/app/perfil";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User existingUser = usersService.findByUsername(username).orElse(null);

        if (existingUser != null){
            existingUser.setNombre(updateUser.getNombre());
            existingUser.setApellidos(updateUser.getApellidos());
        }

        usersService.save(existingUser);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");
        model.addAttribute("usuario", existingUser);
        return "redirect:/app/perfil";
    }


}
