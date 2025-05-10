package ru.unio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.entity.User;
import ru.unio.service.UserPhotoService;

@Controller
@RequestMapping("profile/photos")
public class PhotoController {

    private final UserPhotoService userPhotoService;

    public PhotoController(UserPhotoService userPhotoService) {
        this.userPhotoService = userPhotoService;
    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping
    public String uploadPhoto(@AuthenticationPrincipal User user,
                              @RequestParam("file")MultipartFile photo,
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

//    @PostMapping("/@id/delete")
//    public String deletePhoto(@AuthenticationPrincipal User user) {
//        userPhotoService.deletePhoto(user);
//        return "redirect:/profile";
//    }


}
