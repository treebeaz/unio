package ru.unio.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.entity.User;
import ru.unio.repository.UserRepository;

/**
 * Сервис для управления пользователями системы.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Регистрация новых пользователей</li>
 *   <li>Управление учетными данными</li>
 *   <li>Интеграция с системой безопасности Spring Security</li>
 * </ul>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li>{@link UserRepository} - доступ к данным пользователей</li>
 *   <li>{@link PasswordEncoder} - шифрование паролей</li>
 *   <li>{@link ru.unio.service.CustomUserDetailService} - аутентификация пользователей</li>
 *   <li>{@link ru.unio.config.SecurityConfig} - конфигурация безопасности</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Использует пессимистичные блокировки для предотвращения race condition</li>
 *   <li>Все операции выполняются в транзакционном контексте</li>
 *   <li>Автоматически шифрует пароли перед сохранением</li>
 * </ul>
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userRepository репозиторий для работы с пользователями
     * @param passwordEncoder кодировщик паролей
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * <h4>Алгоритм работы:</h4>
     * <ol>
     *   <li>Блокирует таблицу пользователей для записи</li>
     *   <li>Проверяет уникальность логина с пессимистичной блокировкой</li>
     *   <li>Шифрует пароль с помощью {@link PasswordEncoder}</li>
     *   <li>Сохраняет нового пользователя</li>
     * </ol>
     *
     * <h4>Обработка ошибок:</h4>
     * <ul>
     *   <li>При занятом логине выбрасывает {@link IllegalArgumentException}</li>
     *   <li>При нарушении целостности данных выбрасывает {@link DataIntegrityViolationException}</li>
     * </ul>
     *
     * @param username логин пользователя (должен быть уникальным)
     * @param password пароль в открытом виде (будет зашифрован)
     * @throws IllegalArgumentException если логин уже занят
     * @throws DataIntegrityViolationException при нарушении ограничений базы данных
     */
    @Transactional
    public void registerUser(String username, String password) {
        try {
            userRepository.lockTableForWrite();

            if (userRepository.existsByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Пользователь с таким именем уже существует!");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username already exists", e);
        }
    }
}