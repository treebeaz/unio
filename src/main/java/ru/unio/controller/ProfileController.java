package ru.unio.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

                System.out.println("The file is saved to the path: " + filePath);
            }
            userProfileService.createProfile(user, profileData);
            redirectAttributes.addFlashAttribute("Success", "Profile created successfully");
            return "redirect:/profile";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("Error", "Error creating profile");
            return "redirect:/profile/create";
        }
    }

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
