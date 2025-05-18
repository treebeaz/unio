package ru.unio.repository;

import ru.unio.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    boolean existsByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);
    boolean existsByFirstUserIdAndSecondUserIdOrFirstUserIdAndSecondUserId(
            Long firstUserId1, Long secondUserId1,
            Long firstUserId2, Long secondUserId2);
}