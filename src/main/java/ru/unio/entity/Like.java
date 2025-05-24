package ru.unio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "liked_user_id", nullable = false)
    private User likedUser;

    @Column(name = "liked_time", nullable = false)
    private LocalDateTime likedTime;

    @PrePersist
    protected void onCreate() {
        likedTime = LocalDateTime.now();
    }
}