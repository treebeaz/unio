package ru.unio.service;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class UserPhotoService {

    private final UserPhotoRepository userPhotoRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;

    public UserPhotoService(UserPhotoRepository userPhotoRepository, UserProfileRepository userProfileRepository, UserProfileService userProfileService) {
        this.userPhotoRepository = userPhotoRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileService = userProfileService;
    }

    @Transactional
    public void savePhoto(@AuthenticationPrincipal User user, MultipartFile photo, String uploadDir) throws IOException {
        if(!photo.isEmpty()) {
            String ext = Objects.requireNonNull(photo.getOriginalFilename())
                    .substring(photo.getOriginalFilename().lastIndexOf("."));

            String filename = "user_" + user.getId() +  "_" + user.changeCountPhotos() + ext ;

            Path uploadPath = Paths.get(uploadDir, "photos").toAbsolutePath();
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(filename);
            photo.transferTo(filePath);

            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setUser(user);
            userPhoto.setPhotoUrl("/uploads/photos/" + filename);
            userPhoto.setMain(true);

            userPhotoRepository.save(userPhoto);

            System.out.println("The file is saved to the path: " + filePath);
        }
    }

}
