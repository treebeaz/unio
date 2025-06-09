package ru.unio.entity;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Entity
@Table(name = "user_interests")
@Component
public class UserInterests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "interests", joinColumns = @JoinColumn(name = "user_interests_id"))
    @Column(name = "interest")
    private Set<String> interests;


    public UserInterests() {
    }

    public UserInterests(User user, Set<String> interests) {
        this.user = user;
        this.interests = interests;
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

    public Set<String> getInterests() {
        return interests;
    }

    public void setInterests(Set<String> interests) {
        this.interests = interests;
    }
}
