package ru.unio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.unio.entity.User;
import ru.unio.service.UserProfileService;
import ru.unio.service.UserService;

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
    public String discoverPage(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "0") int index,
                               Model model) {
        if(!userProfileService.profileExists(user)){
            return "redirect:/profile/create";
        }


        List<User> users = userService.getDiscoverableUsers(user.getId());
        if(users.isEmpty()) {
            model.addAttribute("error", "Анкеты закончились");
            return "discover";
        }

        index = normalizeIndex(index, users.size());

        User currentUser = userService.getCurrentUser(user.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("currentIndex", index);

        return "discover";
    }

    private int normalizeIndex(int index, int listSize) {
        if (index < 0) {
            return 0;
        }
        if (index >= listSize) {
            return listSize - 1;
        }
        return index;
    }
} 