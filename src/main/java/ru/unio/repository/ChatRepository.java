package ru.unio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.unio.entity.Chat;
import ru.unio.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByUser1AndUser2(User user1, User user2);
    Optional<Chat> findByUser2AndUser1(User user1, User user2);
    List<Chat> findByUser1OrUser2(User user1, User user2);
}

