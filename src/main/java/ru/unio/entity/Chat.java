package ru.unio.entity;

import jakarta.persistence.*;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    public void setUser1(User user) {
        this.user1 = user;
    }

    public void setUser2(User user) {
        this.user2 = user;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public Long getId() {
        return id;
    }
}
