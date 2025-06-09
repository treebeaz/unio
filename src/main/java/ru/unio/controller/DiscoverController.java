package ru.unio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.unio.entity.User;
import ru.unio.entity.UserProfile;
import ru.unio.service.InterestLoader;
import ru.unio.service.UserProfileService;
import ru.unio.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/discover")
public class DiscoverController {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final InterestLoader interestLoader;

    @Autowired
    public DiscoverController(UserService userService, UserProfileService userProfileService,
                              InterestLoader interestLoader) {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.interestLoader = interestLoader;
    }

    @GetMapping
    public String discoverPage(@AuthenticationPrincipal User user,
                               @RequestParam(defaultValue = "0") int index,
                               @RequestParam(required = false) List<String> interests,
                               @RequestParam(required = false) String gender,
                               @RequestParam(required = false) String city,
                               Model model) {

        if(!userProfileService.profileExists(user)) {
            return "redirect:/profile/create";
        }

        UserProfile userProfile = userProfileService.getUserProfile(user);
        model.addAttribute("userProfile", userProfile);

        // Получаем все доступные интересы для фильтров
        List<String> allInterests = interestLoader.getInterests();
        model.addAttribute("allInterests", allInterests);

        System.out.println("xui");
        System.out.println(interests);
        // Получаем список городов
        List<String> cities = List.of("Москва", "Санкт-Петербург", "Казань", "Новосибирск");
        model.addAttribute("cities", cities);

        // Фильтрация пользователей
        List<User> users = userService.getFilteredUsers(
                user.getId(),
                interests,
                gender,
                city
        );

        if(users.isEmpty()) {
            model.addAttribute("error", "Анкеты закончились");
            return "discover";
        }

        index = normalizeIndex(index, users.size());

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