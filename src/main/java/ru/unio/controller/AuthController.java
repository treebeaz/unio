package ru.unio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.service.UserService;

/**
 *<br> Контроллер аутентификации и регистрации пользователей.
 *<br>
 * <br>Назначение:
 * <br>- Отображение форм логина и регистрации
 * <br>- Обработка регистрации пользователей
 * <br>- Работа с сообщениями об ошибках и успешной регистрации
 *<br>
 * <br>Связан с:
 * <br>- UserService: для создания новых пользователей
 * <br>- login.html и register.html: формы для ввода данных
 * <br>- Spring Security: обрабатывает POST-запрос на /login под капотом
 *<br>
 * <br>Действия:
 * <br>1. Страница входа
 *   <br>- URL: /login
 *   <br>- Метод: GET
 *   <br>- Возвращает: страницу логина (login.html)
 * <br>2. Страница регистрации
 *    <br>- URL: /register
 *    <br>- Метод: GET
 *    <br>- Возвращает: форму регистрации (register.html)
 *    <br>- Передаёт сообщение об ошибке (если есть)
 *<br>
 * <br>3. Обработка формы регистрации
 *    <br>- URL: /register
 *    <br>- Метод: POST
 *    <br>- Параметры: username, password
 *<br>
 *    <br>Что делает:
 *      <br>- Вызывает userService.registerUser(...)
 *      <br>- В случае успеха: редирект на /login?register
 *      <br>- В случае ошибки:
 *          <br>- Сохраняет сообщение об ошибке во flash-атрибутах
 *          <br>- Сохраняет введённый username, чтобы не потерялся
 *          <br>- Возвращает пользователя обратно на страницу /register
 *<br>
 * <br>Аннотации:
 *  <br>- @Controller — сообщает Spring, что это контроллер MVC
 *  <br>- @GetMapping/@PostMapping — обрабатывают HTTP-запросы
 *  <br>- RedirectAttributes — позволяет сохранить данные между редиректами (flash-атрибуты)
 */

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает страницу входа.
     * URL: /login
     * Метод: GET
     * Возвращает: login.html
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Отображает страницу регистрации.
     * URL: /register
     * Метод: GET
     * Возвращает: register.html
     * Добавляет пустое сообщение об ошибке, если оно не передано
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if(!model.containsAttribute("error")) {
            model.addAttribute("error","");
        }
        return "register";
    }

    /**
     * Обрабатывает отправку формы регистрации.
     * URL: /register
     * Метод: POST
     * Параметры: username, password
     *
     * Успех:
     *  - Регистрирует нового пользователя
     *  - Редирект на /login?register
     *
     * Ошибка:
     *  - Добавляет сообщение об ошибке и введённый логин во flash-атрибуты
     *  - Возвращает на страницу регистрации
     */
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
