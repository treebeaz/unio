package ru.unio.repository;

import ru.unio.entity.Match;
import ru.unio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.firstUser = ?1 OR m.secondUser = ?1")
    List<Match> findByUser(User user);

    @Query("SELECT m FROM Match m WHERE (m.firstUser = ?1 AND m.secondUser = ?2) OR (m.firstUser = ?2 AND m.secondUser = ?1)")
    Match findByUsers(User user1, User user2);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.firstUser = ?1 OR m.secondUser = ?1")
    Long countUserMatches(User user);
}