package ru.unio.repository;


import ru.unio.entity.Like;
import ru.unio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByUser(User user);
    List<Like> findByLikedUser(User likedUser);

    @Query("SELECT l FROM Like l WHERE l.user = ?1 AND l.likedUser = ?2")
    Like findByUserAndLikedUser(User user, User likedUser);

    @Query("SELECT l FROM Like l WHERE l.likedUser = ?1 AND l.user = ?2")
    Like findByLikedUserAndUser(User likedUser, User user);
}
