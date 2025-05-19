package ru.unio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.unio.entity.User;
import ru.unio.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/discover")
public class DiscoverController {

    private final UserService userService;

    @Autowired
    public DiscoverController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String discoverPage(Model model) {
        // Get all users except the currently logged-in user
        List<User> users = userService.getAllUsers();
        System.out.println(users);
        model.addAttribute("users", users);
        return "discover";
    }
} 