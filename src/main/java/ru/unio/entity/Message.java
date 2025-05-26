package ru.unio.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Chat chat;

    @ManyToOne
    private User sender;

    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();

    // геттеры и сеттеры
}

