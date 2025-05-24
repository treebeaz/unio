package ru.unio.service;

import ru.unio.entity.Like;
import ru.unio.entity.Match;
import ru.unio.entity.User;
import ru.unio.repository.LikeRepository;
import ru.unio.repository.MatchRepository;
import ru.unio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Like likeUser(Long userId, Long likedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User likedUser = userRepository.findById(likedUserId)
                .orElseThrow(() -> new RuntimeException("Liked user not found"));

        // Проверяем, не лайкнул ли пользователь сам себя
        if (userId.equals(likedUserId)) {
            throw new RuntimeException("Cannot like yourself");
        }

        // Проверяем, не лайкнул ли пользователь уже этого пользователя
        Like existingLike = likeRepository.findByUserAndLikedUser(user, likedUser);
        if (existingLike != null) {
            throw new RuntimeException("User already liked");
        }

        // Создаем новый лайк
        Like like = new Like();
        like.setUser(user);
        like.setLikedUser(likedUser);
        like = likeRepository.save(like);

        // Проверяем, есть ли взаимный лайк (likedUser лайкнул user)
        Like mutualLike = likeRepository.findByUserAndLikedUser(likedUser, user);
        System.out.println("Проверка взаимного лайка: user=" + user.getId() + ", likedUser=" + likedUser.getId());
        System.out.println("Найден mutualLike: " + (mutualLike != null));

        if (mutualLike != null) {
            // Проверяем, нет ли уже мэтча между этими пользователями
            Match existingMatch = matchRepository.findByUsers(user, likedUser);
            if (existingMatch == null) {
                Match match = new Match();
                match.setFirstUser(user.getId() < likedUser.getId() ? user : likedUser);
                match.setSecondUser(user.getId() < likedUser.getId() ? likedUser : user);
                matchRepository.save(match);
                System.out.println("Создан мэтч между user=" + user.getId() + " и likedUser=" + likedUser.getId());
            } else {
                System.out.println("Мэтч уже существует между user=" + user.getId() + " и likedUser=" + likedUser.getId());
            }
        }

        return like;
    }

    public List<Like> getUserLikes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return likeRepository.findByUser(user);
    }

    public List<Like> getUsersWhoLiked(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return likeRepository.findByLikedUser(user);
    }

    public List<Match> getUserMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return matchRepository.findByUser(user);
    }
}