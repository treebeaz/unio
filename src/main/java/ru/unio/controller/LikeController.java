package ru.unio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unio.entity.User;
import ru.unio.service.LikeService;

@Controller
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like/{id}")
    public String likeUser(@AuthenticationPrincipal User user,
                           @PathVariable("id") Long likedUserId,
                           RedirectAttributes redirectAttributes) {
        try {
            likeService.likeUser(user.getId(), likedUserId);
            redirectAttributes.addFlashAttribute("success", "Лайк успешно поставлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/discover";
    }
}