package ru.unio.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.entity.User;
import ru.unio.entity.UserInterests;
import ru.unio.entity.UserPhoto;
import ru.unio.entity.UserProfile;
import ru.unio.repository.UserPhotoRepository;
import ru.unio.service.InterestLoader;
import ru.unio.service.UserPhotoService;
import ru.unio.service.UserProfileService;
import ru.unio.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private InterestLoader interestLoader;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userProfileService сервис для работы с профилями пользователей
     * @param userPhotoRepository репозиторий для работы с фотографиями пользователей
     */
    public ProfileController(UserProfileService userProfileService,
                             UserPhotoRepository userPhotoRepository,
                             UserService userService,
                             UserPhotoService userPhotoService,
                             InterestLoader interestLoader) {
        this.userProfileService = userProfileService;
        this.userPhotoRepository = userPhotoRepository;
        this.userService = userService;
        this.userPhotoService = userPhotoService;
        this.interestLoader = interestLoader;
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
            return "redirect:/discover";
        }

        model.addAttribute("profile", userProfileService.getUserProfile(user));
        Optional<UserPhoto> mainPhoto = userPhotoRepository.findByUserIdAndIsMain(user.getId(), true);
        mainPhoto.ifPresent(userPhoto -> model.addAttribute("mainPhotoUrl", userPhoto.getPhotoUrl()));

        // Получение интересов пользователя
        List<String> interests = userProfileService.getListUserInterests(user);
        model.addAttribute("interests", interests);

        return "profile/view";
    }


    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        Optional<UserPhoto> mainPhoto = userPhotoRepository.findByUserIdAndIsMain(user.getId(), true);

        List<String> allInterests = interestLoader.getInterests();
        List<String> userInterests = userProfileService.getListUserInterests(user);

        model.addAttribute("user", user);
        model.addAttribute("profile", user.getProfile());
        model.addAttribute("allInterests", allInterests);
        model.addAttribute("userInterests", userInterests);

        mainPhoto.ifPresent(userPhoto -> model.addAttribute("photo", userPhoto));
        return "profile/edit-profile";
    }


    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute UserProfile updatedProfile,
            @RequestParam(value = "photo", required = false) MultipartFile newPhoto,
            @RequestParam(value = "interests", required = false) String[] selectedInterests, // Получаем выбранные интересы
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getCurrentUser(principal.getName());

        if (updatedProfile.getBirthDate() != null) {
            updatedProfile.setAge(Period.between(updatedProfile.getBirthDate(), LocalDate.now()).getYears());
        }
        // 1. Обновляем основные данные профиля
        userProfileService.updateProfile(user, updatedProfile);

        // 2. Обновляем интересы
        Set<String> interestsSet = new HashSet<>(Arrays.asList(selectedInterests));
        userProfileService.deleteListUserInterests(user);
        userProfileService.addInterestToUser(user, interestsSet.stream().toList());

        // 3. Замена фото (если выбрано новое)
        if (newPhoto != null && !newPhoto.isEmpty()) {
            try {
                userPhotoService.savePhoto(user, newPhoto, uploadDir); // savePhoto сам обрабатывает замену
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Ошибка загрузки фото");
            }
        }

        redirectAttributes.addFlashAttribute("success", "Профиль обновлен");
        return "redirect:/profile";
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
        if (userProfileService.profileExists(user)) {
            return "redirect:/discover";
        }

        model.addAttribute("profile", new UserProfile());
        model.addAttribute("step", step);

        // Загрузка интересов
        List<String> interests = interestLoader.getInterests();
        model.addAttribute("interests", interests);

        return "profile/create";
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
                                @RequestParam(value = "interests", required = false) List<String> interests,
                                RedirectAttributes redirectAttributes) {

        if (profileData.getBirthDate() != null) {
            profileData.setAge(Period.between(profileData.getBirthDate(), LocalDate.now()).getYears());
        }
        System.out.println(profileData.getAge());

        userProfileService.createProfile(user, profileData, interests);
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