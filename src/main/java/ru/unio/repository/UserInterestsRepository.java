package ru.unio.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.unio.entity.User;
import ru.unio.entity.UserInterests;
import ru.unio.entity.UserPhoto;
import org.springframework.data.jpa.repository.Modifying;


import java.util.Set;

public interface UserInterestsRepository extends JpaRepository<UserInterests, Long> {
    Set<UserInterests> findByUserId(Long id);

    UserInterests findByUser(User user); // Метод для поиска по объекту User


    @Modifying
    @Transactional
    @Query("DELETE FROM UserInterests ui WHERE ui.user.id = :userId")
    void deleteAllInterestsByUser(@Param("userId") Long userId);
}
