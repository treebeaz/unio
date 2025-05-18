package ru.unio.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.entity.User;
import ru.unio.entity.UserPhoto;
import ru.unio.entity.UserProfile;
import ru.unio.repository.UserPhotoRepository;
import ru.unio.service.UserPhotoService;
import ru.unio.service.UserProfileService;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления профилями пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Просмотр и редактирование профиля</li>
 *   <li>Создание нового профиля</li>
 *   <li>Удаление профиля и аккаунта</li>
 *   <li>Управление фотографиями профиля</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li><b>UserProfileService</b> - основной сервис для работы с профилями</li>
 *   <li><b>UserPhotoRepository</b> - репозиторий для работы с фотографиями</li>
 *   <li><b>UserProfile</b> - сущность профиля пользователя</li>
 *   <li><b>User</b> - сущность пользователя</li>
 * </ul>
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserProfileService userProfileService;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userProfileService сервис для работы с профилями пользователей
     * @param userPhotoRepository репозиторий для работы с фотографиями пользователей
     */
    public ProfileController(UserProfileService userProfileService,
                             UserPhotoRepository userPhotoRepository) {
        this.userProfileService = userProfileService;
        this.userPhotoRepository = userPhotoRepository;
    }

    /**
     * Отображает страницу профиля пользователя.
     *
     * <h4>Логика работы:</h4>
     * <ul>
     *   <li>Проверяет существование профиля</li>
     *   <li>Если профиль не существует - перенаправляет на страницу создания</li>
     *   <li>Добавляет в модель данные профиля и главную фотографию</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param model контейнер атрибутов для представления
     * @return имя шаблона для отображения профиля
     */
    @GetMapping
    public String viewProfilePage(@AuthenticationPrincipal User user, Model model) {
        if (!userProfileService.profileExists(user)) {
            return "redirect:/profile/create";
        }

        model.addAttribute("profile", userProfileService.getUserProfile(user));
        Optional<UserPhoto> mainPhoto = userPhotoRepository.findByUserIdAndIsMain(user.getId(), true);

        mainPhoto.ifPresent(userPhoto -> model.addAttribute("mainPhotoUrl", userPhoto.getPhotoUrl()));
        return "profile/view";
    }

    /**
     * Отображает форму создания профиля.
     *
     * <h4>Параметры:</h4>
     * <ul>
     *   <li><code>step</code> - текущий шаг создания профиля (по умолчанию "photo")</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param model контейнер атрибутов для представления
     * @param step текущий шаг создания профиля
     * @return имя шаблона для создания профиля
     */
    @GetMapping("/create")
    public String showCreateProfilePage(@AuthenticationPrincipal User user,
                                        Model model,
                                        @RequestParam(value = "step", defaultValue = "photo") String step) {
        if(userProfileService.profileExists(user)) {
            return "redirect:/profile";
        }

        model.addAttribute("profile", new UserProfile());
        model.addAttribute("step", step);

        return "profile/create";
    }

    /**
     * Отображает форму редактирования профиля.
     *
     * @param user аутентифицированный пользователь
     * @param model контейнер атрибутов для представления
     * @return имя шаблона для редактирования профиля
     */
    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal User user, Model model) {
        UserProfile userProfile = userProfileService.getUserProfile(user);
        model.addAttribute("profile", userProfile);
        return "profile/edit-profile";
    }

    /**
     * Обновляет данные профиля пользователя.
     *
     * <h4>Параметры:</h4>
     * <ul>
     *   <li><code>userProfile</code> - данные профиля</li>
     *   <li><code>photos</code> - фотография профиля (необязательная)</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param userProfile данные профиля
     * @param photos загружаемая фотография
     * @param redirectAttributes атрибуты для перенаправления
     * @return перенаправление на страницу профиля
     */
    @PostMapping
    public String updateProfile(@AuthenticationPrincipal User user,
                                @ModelAttribute UserProfile userProfile,
                                @RequestParam(value = "photo_url", required = false) MultipartFile photos,
                                RedirectAttributes redirectAttributes) {
        try {
            userProfileService.updateUserProfile(user, userProfile, photos);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Profile update failed");
            return "redirect:/profile/edit";
        }
    }

    /**
     * Создает новый профиль пользователя.
     *
     * @param user аутентифицированный пользователь
     * @param profileData данные нового профиля
     * @param redirectAttributes атрибуты для перенаправления
     * @return перенаправление на страницу профиля
     */
    @PostMapping("/create")
    public String createProfile(@AuthenticationPrincipal User user,
                                @ModelAttribute UserProfile profileData,
                                RedirectAttributes redirectAttributes) {
        userProfileService.createProfile(user, profileData);
        redirectAttributes.addFlashAttribute("step", "info");

        return "redirect:/profile";
    }

    /**
     * Удаляет профиль и аккаунт пользователя.
     *
     * <h4>Логика работы:</h4>
     * <ul>
     *   <li>Удаляет профиль через UserProfileService</li>
     *   <li>Выполняет выход пользователя из системы</li>
     *   <li>Перенаправляет на страницу входа</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param redirectAttributes атрибуты для перенаправления
     * @return перенаправление на страницу входа
     */
    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal User user,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        try {
            userProfileService.deleteUserWithProfile(user);

            new SecurityContextLogoutHandler().logout(request, response,
                    SecurityContextHolder.getContext().getAuthentication());

            redirectAttributes.addFlashAttribute("Success", "Profile deleted successfully");
            return "redirect:/login?status=deleted";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("Error", "Error deleting profile");
            return "redirect:/profile";
        }
    }
}