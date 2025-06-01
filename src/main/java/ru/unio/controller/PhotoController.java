package ru.unio.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.entity.User;
import ru.unio.service.UserPhotoService;

/**
 * Контроллер для управления фотографиями пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Загрузка фотографий пользователей</li>
 *   <li>Управление путями хранения фотографий</li>
 *   <li>Обработка ошибок при загрузке</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li><b>UserPhotoService</b> - сервис для работы с фотографиями пользователей</li>
 *   <li><b>User</b> - сущность пользователя</li>
 *   <li><b>MultipartFile</b> - интерфейс для работы с загружаемыми файлами</li>
 * </ul>
 *
 * <h3>Конфигурация:</h3>
 * <ul>
 *   <li>Путь загрузки файлов настраивается через свойство <code>app.upload.dir</code></li>
 * </ul>
 */
@Controller
@RequestMapping("profile/photos")
public class PhotoController {

    private final UserPhotoService userPhotoService;

    /**
     * Конструктор с внедрением зависимости UserPhotoService.
     *
     * @param userPhotoService сервис для работы с фотографиями пользователей
     */
    public PhotoController(UserPhotoService userPhotoService) {
        this.userPhotoService = userPhotoService;
    }

    /**
     * Директория для загрузки фотографий.
     * <p>
     * Значение берется из конфигурации приложения (application.properties/yml)
     * с помощью аннотации {@code @Value("${app.upload.dir}")}.
     * </p>
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Обрабатывает загрузку фотографии пользователя.
     *
     * <h4>Параметры запроса:</h4>
     * <ul>
     *   <li><code>file</code> - файл фотографии (MultipartFile)</li>
     * </ul>
     *
     * <h4>Логика работы:</h4>
     * <ol>
     *   <li>Получает аутентифицированного пользователя</li>
     *   <li>Пытается сохранить фотографию через UserPhotoService</li>
     *   <li>При успехе добавляет flash-атрибут "success"</li>
     *   <li>При ошибке добавляет flash-атрибут "error"</li>
     *   <li>Перенаправляет на страницу профиля</li>
     * </ol>
     *
     * <h4>Возможные ошибки:</h4>
     * <ul>
     *   <li>Недопустимый формат файла</li>
     *   <li>Превышение максимального размера файла</li>
     *   <li>Проблемы с файловой системой</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param photo загружаемый файл фотографии
     * @param redirectAttributes атрибуты для перенаправления
     * @return строка перенаправления на страницу профиля
     */
    @PostMapping
    public String uploadPhoto(@AuthenticationPrincipal User user,
                              @RequestParam("file") MultipartFile photo,
                              RedirectAttributes redirectAttributes) {
        try {
            userPhotoService.savePhoto(user, photo, uploadDir);
            redirectAttributes.addFlashAttribute("success", "Photo uploaded");
            redirectAttributes.addFlashAttribute("step", "info");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Photo upload failed");
        }

        return "redirect:/profile/create?step=info";
    }

    /**
     * Заготовка метода для удаления фотографии пользователя (закомментирована).
     *
     * <h4>Предполагаемая функциональность:</h4>
     * <ul>
     *   <li>Удаление фотографии пользователя</li>
     *   <li>Перенаправление на страницу профиля</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @return строка перенаправления на страницу профиля
     */
    /*
    @PostMapping("/{id}/delete")
    public String deletePhoto(@AuthenticationPrincipal User user) {
        userPhotoService.deletePhoto(user);
        return "redirect:/profile";
    }
    */
}