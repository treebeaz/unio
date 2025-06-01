package ru.unio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getLikedUser() {
        return likedUser;
    }

    public void setLikedUser(User likedUser) {
        this.likedUser = likedUser;
    }

    public LocalDateTime getLikedTime() {
        return likedTime;
    }

    public void setLikedTime(LocalDateTime likedTime) {
        this.likedTime = likedTime;
    }
}