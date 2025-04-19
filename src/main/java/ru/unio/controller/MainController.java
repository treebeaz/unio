package ru.unio.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.unio.entity.User;
//import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String home (@RequestParam (required = false) String error, Model model) {
        if(error != null) {
            model.addAttribute("error", error);
        }
        return "home";
    }

    @GetMapping("/profile")
    public String profile (@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile";
    }
}