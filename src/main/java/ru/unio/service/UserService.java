package ru.unio.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.entity.User;
import ru.unio.repository.UserRepository;

/**
 * UserService - это сервис для работы с пользователями. Он отвечает за:
 * Регистрацию новых пользователей
 * Поиск пользователей по логину
 * Шифрование паролей
 *
 *
 *
 *
 */

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя в системе.
     * @param username
     * @param password
     *
     * Как работает:
     * Блокирует таблицу пользователей от изменений (чтобы избежать проблем при одновременной регистрации)
     * Проверяет, не занят ли логин
     * Если логин свободен - создаёт пользователя с зашифрованным паролем
     */
    @Transactional
    public void registerUser(String username, String password) {

        try{
            userRepository.lockTableForWrite();

            if(userRepository.existsByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Пользователь с таким именем уже существует!");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username already exists", e);
        }
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username not found"));
    }

}
