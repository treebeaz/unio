package ru.unio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.unio.entity.User;
import ru.unio.service.UserService;
import ru.unio.service.UserProfileService;

import java.util.List;

@Controller
@RequestMapping("/discover")
public class DiscoverController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @Autowired
    public DiscoverController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public String discoverPage(@AuthenticationPrincipal User user, Model model) {
        if (!userProfileService.profileExists(user)) {
            return "redirect:/profile/create";
        }

        List<User> users = userService.getAllUsers();
        System.out.println(users);
        model.addAttribute("users", users);
        return "discover";
    }
} 