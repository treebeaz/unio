package ru.unio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.unio.entity.User;
import ru.unio.entity.UserPhoto;
import ru.unio.entity.UserProfile;
import ru.unio.repository.UserPhotoRepository;
import ru.unio.repository.UserProfileRepository;
import ru.unio.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository, UserPhotoRepository userPhotoRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.userPhotoRepository = userPhotoRepository;
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
    public void updateUserProfile(@AuthenticationPrincipal User user,
                                  UserProfile profile,
                                  @RequestParam(value = "photo_url", required = false) MultipartFile photos) {

    }

    @Transactional
    public void deleteUserWithProfile(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Unable to find user when deleting profile"));
        userProfileRepository.delete(profile);
        List<UserPhoto> userPhotos = userPhotoRepository.findByUserId(profile.getUser().getId());
        userPhotoRepository.deleteAll(userPhotos);
    }

}
