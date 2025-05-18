package ru.unio.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.entity.User;
import ru.unio.exception.UserNotFoundException;
import ru.unio.repository.UserRepository;

/**
 * <br>Сервис для управления пользователями системы.
 * <br>
 * <br>Основные функции:
 * <br>- Регистрация новых пользователей с проверкой уникальности логина
 * <br>- Шифрование паролей перед сохранением (PasswordEncoder)
 * <br>- Интеграция с системой безопасности Spring Security
 * <br>
 * <br>Важные особенности:
 * <br>- Использует пессимистичную блокировку (lockTableForWrite) для защиты от race condition
 * <br>- Обрабатывает ошибки, связанные с нарушением уникальности логина
 * <br>- Вся регистрация обернута в транзакцию (@Transactional), чтобы гарантировать атомарность
 * <br>
 * <br>Использует:
 * <br>- UserRepository для доступа к данным в Postgres
 * <br>- PasswordEncoder (обычно BCrypt), внедренный через Spring Security для безопасного хранения паролей
 * <br>
 * <br>В связке с:
 * <br>- `User`: сущность, которая сохраняется в БД
 * <br>- `CustomUserDetailService`: отвечает за аутентификацию (логин)
 * <br>- `SecurityConfig`: конфигурирует Spring Security (шифрование, авторизация, фильтры)
 * <br>
 * <br>Аннотация @Transactional - Это аннотация из Spring Framework, которая управляет транзакциями. Она говорит Spring-у: "всё, что происходит в этом методе, должно выполняться как единая операция".
 * Если внутри метода что-то пойдёт не так (например, произойдёт исключение), то все изменения в базе данных будут отменены — как будто метод и не вызывался.
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
     * <br>Регистрирует нового пользователя в системе.
     * <br>
     * <br>Алгоритм работы:
     * <br>1. Блокирует таблицу пользователей для записи
     * <br>2. Проверяет, не занят ли логин
     * <br>3. Если логин свободен - создает пользователя с зашифрованным паролем
     * <br>4. Если логин занят - выбрасывает исключение
     * <br>
     * <br>@param username логин пользователя (должен быть уникальным)
     * <br>@param password пароль в открытом виде (будет зашифрован)
     * <br>@throws IllegalArgumentException если логин уже занят
     *
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

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
    }

}
