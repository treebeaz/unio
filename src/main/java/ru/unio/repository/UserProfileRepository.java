package ru.unio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.unio.entity.User;
import ru.unio.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
    boolean existsByUser(User user);
}
