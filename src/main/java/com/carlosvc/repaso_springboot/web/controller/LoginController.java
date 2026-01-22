package com.carlosvc.repaso_springboot.web.controller;

import com.carlosvc.repaso_springboot.rest.users.models.User;
import com.carlosvc.repaso_springboot.rest.users.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UsersService usersService;

    @GetMapping("/")
    public String welcome() {
        return "redirect:/public/";
    }


    @GetMapping("/auth/login")
    public String login(Model model) {
        model.addAttribute("usuario", new User());
        return "login";
    }
}
