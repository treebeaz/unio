package ru.unio.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.unio.entity.User;
import ru.unio.entity.UserPhoto;
import ru.unio.entity.UserProfile;
import ru.unio.repository.UserPhotoRepository;
import ru.unio.repository.UserProfileRepository;
import ru.unio.repository.UserRepository;

import java.util.List;

/**
 * Сервис для управления профилями пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Создание и обновление профилей пользователей</li>
 *   <li>Управление связью между пользователями и их профилями</li>
 *   <li>Обработка фотографий профилей</li>
 *   <li>Удаление профилей и связанных данных</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li>{@link UserProfileRepository} - работа с данными профилей</li>
 *   <li>{@link UserRepository} - управление пользователями</li>
 *   <li>{@link UserPhotoRepository} - работа с фотографиями профилей</li>
 * </ul>
 */
@Service
@Transactional
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userProfileRepository репозиторий для работы с профилями
     * @param userRepository репозиторий для работы с пользователями
     * @param userPhotoRepository репозиторий для работы с фотографиями
     */
    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserRepository userRepository,
                              UserPhotoRepository userPhotoRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.userPhotoRepository = userPhotoRepository;
    }

    /**
     * Создает новый профиль для пользователя.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Использует пессимистичную блокировку для предотвращения race condition</li>
     *   <li>Связывает профиль с существующим пользователем</li>
     *   <li>Выполняется в транзакции</li>
     * </ul>
     *
     * @param user пользователь, для которого создается профиль
     * @param profile данные профиля
     * @throws RuntimeException если пользователь не найден
     */
    public void createProfile(User user, UserProfile profile) {
        userProfileRepository.findAndLockByUsername(user.getUsername());

        User attachedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Unable to find user when creating profile"));

        profile.setUser(attachedUser);
        userProfileRepository.save(profile);
    }

    /**
     * Получает профиль пользователя. Если профиль не существует - создает новый.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Гарантирует возврат профиля (создает новый при необходимости)</li>
     *   <li>Выполняется в транзакции</li>
     * </ul>
     *
     * @param user пользователь, чей профиль требуется получить
     * @return существующий или новый профиль пользователя
     */
    public UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile();
                    profile.setUser(user);
                    return userProfileRepository.save(profile);
                });
    }

    /**
     * Проверяет существование профиля для пользователя.
     *
     * @param user пользователь для проверки
     * @return true если профиль существует, false в противном случае
     */
    public boolean profileExists(User user) {
        return userProfileRepository.existsByUser(user);
    }

    /**
     * Обновляет профиль пользователя.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Может обновлять фотографию профиля</li>
     *   <li>Выполняется в транзакции</li>
     * </ul>
     *
     * @param user аутентифицированный пользователь
     * @param profile новые данные профиля
     * @param photos новая фотография профиля (опционально)
     */
    public void updateUserProfile(User user, UserProfile profile, MultipartFile photos) {
        // Реализация обновления профиля
    }

    /**
     * Удаляет пользователя и все связанные с ним данные.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Удаляет профиль пользователя</li>
     *   <li>Удаляет все фотографии пользователя</li>
     *   <li>Выполняется в транзакции</li>
     * </ul>
     *
     * @param user пользователь для удаления
     * @throws RuntimeException если профиль пользователя не найден
     */
    public void deleteUserWithProfile(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Unable to find user when deleting profile"));
        userProfileRepository.delete(profile);
        List<UserPhoto> userPhotos = userPhotoRepository.findByUserId(profile.getUser().getId());
        userPhotoRepository.deleteAll(userPhotos);
    }
}