package ru.unio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.unio.entity.Chat;
import ru.unio.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderByTimestampAsc(Chat chat);
    List<Message> findByChatAndIdGreaterThanOrderByTimestampAsc(Chat chat, Long id);

}