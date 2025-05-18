package ru.unio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.dto.LikeDto;
import ru.unio.entity.Like;
import ru.unio.entity.User;
import ru.unio.exception.LikeException;
import ru.unio.repository.LikeRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final MatchService matchService;

    @Transactional
    public LikeDto addLike(Long userId, Long likedUserId) {
        if (userId.equals(likedUserId)) {
            throw new LikeException("Нельзя лайкнуть самого себя");
        }

        if (likeRepository.existsByUserIdAndLikedUserId(userId, likedUserId)) {
            throw new LikeException("Вы уже лайкали этого пользователя");
        }

        User user = userService.findById(userId);
        User likedUser = userService.findById(likedUserId);

        Like like = new Like();
        like.setUser(user);
        like.setLikedUser(likedUser);
        like.setLikedTime(LocalDateTime.now());
        like = likeRepository.save(like);

        checkForMutualLike(likedUserId, user, likedUser);

        likeRepository.findByLikedUserIdAndUserId(likedUserId, userId)
                .ifPresent(mutualLike -> {
                    // Создаем мэтч, если нашли взаимный лайк
                    matchService.createMatch(user, likedUser);
                });

        return convertToDto(like);
    }

    private void checkForMutualLike(Long likedUserId, User user, User likedUser) {
        likeRepository.findByLikedUserIdAndUserId(likedUserId, user.getId())
                .ifPresent(mutualLike -> matchService.createMatch(user, likedUser));
    }

    private LikeDto convertToDto(Like like) {
        return new LikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getLikedUser().getId(),
                like.getLikedTime()
        );
    }
}