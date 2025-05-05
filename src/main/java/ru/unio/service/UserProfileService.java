package ru.unio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.entity.User;
import ru.unio.entity.UserProfile;
import ru.unio.repository.UserProfileRepository;
import ru.unio.repository.UserRepository;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createProfile(User user, UserProfile profile) {
        userProfileRepository.findAndLockByUsername(user.getUsername());

        User attachedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Unable to find user when creating profile"));

        profile.setUser(attachedUser);  // Связываем профиль с пользователем
        userProfileRepository.save(profile);  // Сохраняем в БД
    }

    @Transactional
    public UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile();
                    profile.setUser(user);
                    return userProfileRepository.save(profile);
                });
    }

    public boolean profileExists(User user) {
        return userProfileRepository.existsByUser(user);
    }

    @Transactional
    public void deleteUserWithProfile(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Unable to find user when deleting profile"));
        userProfileRepository.delete(profile);
    }

}
