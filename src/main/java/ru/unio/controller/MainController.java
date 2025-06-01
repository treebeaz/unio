package ru.unio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Основной контроллер приложения, обрабатывающий главную страницу.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Отображение главной страницы приложения</li>
 *   <li>Обработка параметров ошибок для отображения на главной странице</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li><b>home.html</b> - шаблон главной страницы</li>
 *   <li><b>Spring Security</b> - для обработки аутентификации</li>
 * </ul>
 */
@Controller
public class MainController {

    /**
     * Обрабатывает запрос на главную страницу приложения.
     *
     * <h4>Параметры:</h4>
     * <ul>
     *   <li><code>error</code> - необязательный параметр, содержащий сообщение об ошибке</li>
     * </ul>
     *
     * <h4>Логика работы:</h4>
     * <ul>
     *   <li>Если передан параметр <code>error</code>, добавляет его в модель</li>
     *   <li>Всегда возвращает шаблон главной страницы</li>
     * </ul>
     *
     * <h4>Примеры использования:</h4>
     * <ul>
     *   <li><code>/</code> - отображает главную страницу без ошибок</li>
     *   <li><code>/?error=Authentication_failed</code> - отображает главную страницу с сообщением об ошибке</li>
     * </ul>
     *
     * @param error необязательное сообщение об ошибке
     * @param model контейнер атрибутов для передачи данных в представление
     * @return имя шаблона главной страницы ("home")
     */
    @GetMapping("/")
    public String home(@RequestParam(required = false) String error, Model model) {
        if(error != null) {
            model.addAttribute("error", error);
        }
        return "home";
    }
}