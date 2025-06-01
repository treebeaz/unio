package ru.unio.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.unio.entity.Match;
import ru.unio.entity.User;
import ru.unio.service.LikeService;

import java.util.List;

@Controller
public class MatchController {
    private final LikeService likeService;

    public MatchController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/matches")
    public String getMatches(@AuthenticationPrincipal User user, Model model) {
        List<Match> matches = likeService.getUserMatches(user.getId());
        model.addAttribute("matches", matches);
        return "matches";
    }
} 