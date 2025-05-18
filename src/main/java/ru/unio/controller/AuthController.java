package ru.unio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.service.UserService;

/**
 * Контроллер для обработки аутентификации и регистрации пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Отображение форм входа и регистрации</li>
 *   <li>Обработка данных регистрации</li>
 *   <li>Управление сообщениями об ошибках</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li><b>UserService</b> - сервис для регистрации новых пользователей</li>
 *   <li><b>login.html</b> - шаблон страницы входа</li>
 *   <li><b>register.html</b> - шаблон страницы регистрации</li>
 *   <li><b>Spring Security</b> - обработка POST /login</li>
 * </ul>
 *
 * <h3>Поток данных:</h3>
 * <ol>
 *   <li>Пользователь запрашивает страницу входа/регистрации</li>
 *   <li>Контроллер возвращает соответствующий HTML-шаблон</li>
 *   <li>При отправке формы регистрации данные валидируются</li>
 *   <li>Успешная регистрация → редирект на страницу входа</li>
 *   <li>Ошибка → сохранение состояния формы и сообщения об ошибке</li>
 * </ol>
 *
 * @see UserService
 */
@Controller
public class AuthController {
    private final UserService userService;

    /**
     * Конструктор с внедрением зависимости UserService.
     *
     * @param userService сервис для работы с пользователями
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает страницу входа в систему.
     *
     * <h4>Детали реализации:</h4>
     * <ul>
     *   <li>URL: <code>/login</code></li>
     *   <li>HTTP метод: GET</li>
     *   <li>Возвращает: <code>login.html</code></li>
     * </ul>
     *
     * @return имя шаблона страницы входа
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Отображает страницу регистрации нового пользователя.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>URL: <code>/register</code></li>
     *   <li>HTTP метод: GET</li>
     *   <li>Возвращает: <code>register.html</code></li>
     *   <li>Добавляет в модель пустое сообщение об ошибке, если оно отсутствует</li>
     * </ul>
     *
     * @param model контейнер атрибутов представления
     * @return имя шаблона страницы регистрации
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
     *
     * <h4>Бизнес-логика:</h4>
     * <ul>
     *   <li>Пытается зарегистрировать нового пользователя через UserService</li>
     *   <li>При успехе - перенаправляет на страницу входа с параметром register</li>
     *   <li>При ошибке - сохраняет сообщение об ошибке и введенные данные</li>
     * </ul>
     *
     * <h4>Параметры запроса:</h4>
     * <ul>
     *   <li><code>username</code> - логин пользователя</li>
     *   <li><code>password</code> - пароль пользователя</li>
     * </ul>
     *
     * @param username логин пользователя
     * @param password пароль пользователя
     * @param redirectAttributes атрибуты для перенаправления
     * @return строка перенаправления
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
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