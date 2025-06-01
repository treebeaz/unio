package ru.unio.service;

import ru.unio.entity.Chat;
import ru.unio.entity.Like;
import ru.unio.entity.Match;
import ru.unio.entity.User;
import ru.unio.repository.ChatRepository;
import ru.unio.repository.LikeRepository;
import ru.unio.repository.MatchRepository;
import ru.unio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LikeService {
    private final ChatRepository chatRepository;
    private final LikeRepository likeRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public LikeService(ChatRepository chatRepository,
                       LikeRepository likeRepository,
                       MatchRepository matchRepository,
                       UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.likeRepository = likeRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Like likeUser(Long userId, Long likedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User likedUser = userRepository.findById(likedUserId)
                .orElseThrow(() -> new RuntimeException("Liked user not found"));

        if (userId.equals(likedUserId)) {
            throw new RuntimeException("Cannot like yourself");
        }

        Like existingLike = likeRepository.findByUserAndLikedUser(user, likedUser);
        if (existingLike != null) {
            throw new RuntimeException("User already liked");
        }

        Like like = new Like();
        like.setUser(user);
        like.setLikedUser(likedUser);
        like = likeRepository.save(like);

        Like mutualLike = likeRepository.findByUserAndLikedUser(likedUser, user);
        System.out.println("Проверка взаимного лайка: user=" + user.getId() + ", likedUser=" + likedUser.getId());
        System.out.println("Найден mutualLike: " + (mutualLike != null));

        if (mutualLike != null) {
            Match existingMatch = matchRepository.findByUsers(user, likedUser);
//            System.out.println("krut1");

//            if (existingMatch == null) {
            System.out.println("krut2");
            Match match = new Match();
            match.setFirstUser(user.getId() < likedUser.getId() ? user : likedUser);
            match.setSecondUser(user.getId() < likedUser.getId() ? likedUser : user);
            matchRepository.save(match);
            System.out.println("Создан мэтч между user=" + user.getId() + " и likedUser=" + likedUser.getId());
            Chat chat = new Chat();
            chat.setUser1(user.getId() < likedUser.getId() ? user : likedUser);
            chat.setUser2(user.getId() < likedUser.getId() ? likedUser : user);
            chatRepository.save(chat);
            System.out.println("Создан чат между user=" + user.getId() + " и likedUser=" + likedUser.getId());

//            } else {
//                System.out.println("Мэтч уже существует между user=" + user.getId() + " и likedUser=" + likedUser.getId());
//            }
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