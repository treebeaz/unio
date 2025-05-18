package ru.unio.repository;

import ru.unio.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndLikedUserId(Long userId, Long likedUserId);

    @Query("SELECT l FROM Like l WHERE l.likedUser.id = :userId AND l.user.id = :likedUserId")
    Optional<Like> findMutualLike(@Param("userId") Long userId,
                                  @Param("likedUserId") Long likedUserId);

    Optional<Like> findByLikedUserIdAndUserId(Long likedUserId, Long userId);
}