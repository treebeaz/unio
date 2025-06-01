package ru.unio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_user_id", nullable = false)
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_user_id", nullable = false)
    private User secondUser;

    @Column(name = "matched_time", nullable = false)
    private LocalDateTime matchedTime;

    @PrePersist
    protected void onCreate() {
        matchedTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public LocalDateTime getMatchedTime() {
        return matchedTime;
    }

    public void setMatchedTime(LocalDateTime matchedTime) {
        this.matchedTime = matchedTime;
    }
}
