package ru.unio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.entity.Match;
import ru.unio.entity.User;
import ru.unio.repository.MatchRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;

    @Transactional
    public Match createMatch(User user1, User user2) {
        Match match = new Match();
        match.setFirstUser(user1);
        match.setSecondUser(user2);
        match.setMatchedTime(LocalDateTime.now());
        return matchRepository.save(match);
    }
}