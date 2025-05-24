package ru.unio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
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
}
