package ru.unio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.service.UserService;

/**
 * AuthController - это контроллер, который отвечает за:
 * Показ страницы входа (логина)
 * Показ страницы регистрации
 * Обработку регистрации новых пользователей
 *
 * Страница входа
 * Адрес: /login
 * Метод: GET
 * Что делает:
 * Показывает форму для входа в систему
 *
 * форма это ес че html страницы.
 *
 * Страница регистрации
 * Адрес: /register
 * Метод: GET
 * Что делает:
 * Показывает форму для регистрации нового пользователя
 * Может отображать сообщения об ошибках, если они были
 *
 * Обработка форм
 * Регистрация пользователя
 * Адрес: /register
 * Метод: POST
 * Параметры:
 * username - логин пользователя (обязательно)
 * password - пароль пользователя (обязательно)
 *
 * Что происходит при успехе:
 * Создаётся новый пользователь
 * Происходит перенаправление на страницу входа (/login)
 * Добавляется параметр ?register для показа сообщения об успехе
 *
 * Что происходит при ошибке:
 * Сохраняется сообщение об ошибке
 * Сохраняется введённый username (чтобы не вводить заново)
 * Происходит перенаправление обратно на страницу регистрации
 *
 * Как это работает
 * Использует UserService для регистрации пользователей
 * При ошибках сохраняет данные через RedirectAttributes (чтобы они не потерялись при перенаправлении)
 * Все операции с формами выполняются через POST-запросы (безопасно)
 */

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if(!model.containsAttribute("error")) {
            model.addAttribute("error","");
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, password);
            return "redirect:/login?register";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/register";
        }
    }

}
