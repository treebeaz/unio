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
import java.util.Optional;


@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserProfileService userProfileService;
    private final UserPhotoRepository userPhotoRepository;

    public ProfileController(UserProfileService userProfileService, UserPhotoRepository userPhotoRepository) {
        this.userProfileService = userProfileService;
        this.userPhotoRepository = userPhotoRepository;
    }

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

    @GetMapping("/create")
    public String showCreateProfilePage(@AuthenticationPrincipal User user, Model model) {
        if(userProfileService.profileExists(user)) {
            return "redirect:/profile";
        }

        model.addAttribute("profile", new UserProfile());
        return "profile/create";
    }

    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal User user, Model model) {
        UserProfile userProfile = userProfileService.getUserProfile(user);
        model.addAttribute("profile", userProfile);
        return "profile/edit-profile";
    }

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

    @PostMapping("/create")
    public String createProfile(@AuthenticationPrincipal User user,
                                @ModelAttribute UserProfile profileData) {
        userProfileService.createProfile(user, profileData);

        return "redirect:/profile";
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
