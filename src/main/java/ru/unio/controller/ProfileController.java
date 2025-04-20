package ru.unio.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.entity.User;
import ru.unio.entity.UserProfile;
import ru.unio.repository.UserRepository;
import ru.unio.service.UserProfileService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    public ProfileController(UserProfileService userProfileService, UserRepository userRepository) {
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String viewProfilePage(@AuthenticationPrincipal User user, Model model) {
        if (!userProfileService.profileExists(user)) {
            return "redirect:/profile/create";
        }

        model.addAttribute("profile", userProfileService.getUserProfile(user));
        return "profile/view";
    }

    @GetMapping("/create")
    public String showCreateProfilePage(@AuthenticationPrincipal User user, Model model) {
        if(userProfileService.profileExists(user)) {
            return "redirect:/profile";
        }

        model.addAttribute("profile", new UserProfile());
        return "profile/create";
    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping("/create")
    public String createProfile(@AuthenticationPrincipal User user,
                                @ModelAttribute UserProfile profileData,
                                @RequestParam("photo_url") MultipartFile photo,
                                RedirectAttributes redirectAttributes) {
        try {
            if(!photo.isEmpty()) {
                String ext = Objects.requireNonNull(photo.getOriginalFilename())
                        .substring(photo.getOriginalFilename().lastIndexOf("."));
                String filename = "user_" + user.getId() + ext;

                Path uploadPath = Paths.get(uploadDir, "photos").toAbsolutePath();
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(filename);
                photo.transferTo(filePath);
                profileData.setPhotoUrl("/uploads/photos/" + filename);

                System.out.println("Файл сохранен по пути: " + filePath);
            }
            userProfileService.createProfile(user, profileData);
            redirectAttributes.addFlashAttribute("Success", "Профиль успешно создан");
            return "redirect:/profile";
        }
        catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("Error", "Ошибка при создании профиля");
            return "redirect:/profile/create";
        }
    }

}
