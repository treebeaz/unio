package ru.unio.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.unio.entity.User;
import ru.unio.entity.UserPhoto;
import ru.unio.repository.UserPhotoRepository;
import ru.unio.repository.UserProfileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Сервис для работы с фотографиями пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Сохранение фотографий пользователей на сервере</li>
 *   <li>Управление связью фотографий с профилями пользователей</li>
 *   <li>Обработка и валидация загружаемых файлов</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li>{@link UserPhotoRepository} - хранение информации о фотографиях</li>
 *   <li>{@link UserProfileRepository} - обновление профилей пользователей</li>
 *   <li>{@link UserProfileService} - управление профилями</li>
 * </ul>
 */
@Service
public class UserPhotoService {

    private final UserPhotoRepository userPhotoRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userPhotoRepository репозиторий для работы с фотографиями
     * @param userProfileRepository репозиторий для работы с профилями
     * @param userProfileService сервис для управления профилями
     */
    public UserPhotoService(UserPhotoRepository userPhotoRepository,
                            UserProfileRepository userProfileRepository,
                            UserProfileService userProfileService) {
        this.userPhotoRepository = userPhotoRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileService = userProfileService;
    }

    /**
     * Сохраняет фотографию пользователя.
     *
     * <h4>Процесс работы:</h4>
     * <ol>
     *   <li>Проверяет, что файл не пустой</li>
     *   <li>Генерирует уникальное имя файла на основе ID пользователя</li>
     *   <li>Создает директорию для хранения, если она не существует</li>
     *   <li>Сохраняет файл на сервере</li>
     *   <li>Создает запись о фотографии в базе данных</li>
     *   <li>Помечает фотографию как главную для профиля</li>
     * </ol>
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Выполняется в транзакции ({@code @Transactional})</li>
     *   <li>Файлы сохраняются в поддиректории {@code photos}</li>
     *   <li>Имена файлов содержат ID пользователя для уникальности</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param photo загружаемый файл фотографии
     * @param uploadDir базовый каталог для загрузки
     * @throws IOException при ошибках работы с файловой системой
     * @throws NullPointerException если имя файла не содержит расширения
     */
    @Transactional
    public void savePhoto(User user, MultipartFile photo, String uploadDir) throws IOException {
        if(!photo.isEmpty()) {
            // Генерация уникального имени файла
            String ext = Objects.requireNonNull(photo.getOriginalFilename())
                    .substring(photo.getOriginalFilename().lastIndexOf("."));

            String filename = "user_" + user.getId() + "_" + user.changeCountPhotos() + ext;

            // Создание директории для хранения
            Path uploadPath = Paths.get(uploadDir, "photos").toAbsolutePath();
            Files.createDirectories(uploadPath);

            // Сохранение файла
            Path filePath = uploadPath.resolve(filename);
            photo.transferTo(filePath);

            // Создание записи в базе данных
            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setUser(user);
            userPhoto.setPhotoUrl("/uploads/photos/" + filename);
            userPhoto.setMain(true);

            userPhotoRepository.save(userPhoto);

            System.out.println("Файл сохранен по пути: " + filePath);
        }
    }
}