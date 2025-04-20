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
        User attachedUser = userRepository.findByUsername(user.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));

        profile.setUser(attachedUser);  // Связываем профиль с пользователем
        userProfileRepository.save(profile);  // Сохраняем в БД
    }

    @Transactional
    public void updateProfile(User user, UserProfile newProfileData) {
        UserProfile existingProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Профиль не найден"));

        // Обновляем поля
        existingProfile.setName(newProfileData.getName());
        existingProfile.setBirthDate(newProfileData.getBirthDate());
        existingProfile.setGender(newProfileData.getGender());
        existingProfile.setCity(newProfileData.getCity());
        existingProfile.setBio(newProfileData.getBio());

        userProfileRepository.save(existingProfile);  // Сохраняем изменения
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
}
