package ru.unio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.unio.dto.LikeDto;
import ru.unio.service.LikeService;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeDto> likeUser(
            @RequestParam Long userId,
            @RequestParam Long likedUserId) {
        LikeDto like = likeService.addLike(userId, likedUserId);
        return ResponseEntity.ok(like);
    }
}