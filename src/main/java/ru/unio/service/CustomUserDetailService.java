package ru.unio.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.unio.repository.UserRepository;

/**
 * Сервис аутентификации пользователей для Spring Security.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Загрузка данных пользователя по имени пользователя</li>
 *   <li>Преобразование сущности {@link ru.unio.entity.User} в {@link UserDetails}</li>
 *   <li>Интеграция между Spring Security и системой хранения пользователей</li>
 * </ul>
 *
 * <h3>Жизненный цикл аутентификации:</h3>
 * <ol>
 *   <li>Пользователь отправляет учетные данные на /login</li>
 *   <li>Spring Security вызывает {@code loadUserByUsername()}</li>
 *   <li>Сервис ищет пользователя в {@link UserRepository}</li>
 *   <li>При успехе возвращает {@link UserDetails} для проверки пароля</li>
 *   <li>При ошибке выбрасывает {@link UsernameNotFoundException}</li>
 * </ol>
 *
 * <h3>Архитектурные связи:</h3>
 * <ul>
 *   <li>Реализует стандартный интерфейс {@link UserDetailsService}</li>
 *   <li>Использует {@link UserRepository} для доступа к данным</li>
 *   <li>Работает с сущностью {@link ru.unio.entity.User}, которая реализует {@link UserDetails}</li>
 *   <li>Интегрируется с {@link ru.unio.config.SecurityConfig}</li>
 * </ul>
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Конструктор с внедрением зависимости UserRepository.
     *
     * @param userRepository репозиторий для работы с пользователями
     */
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает пользователя по имени пользователя.
     *
     * <h4>Процесс работы:</h4>
     * <ol>
     *   <li>Ищет пользователя в базе данных по username</li>
     *   <li>Если пользователь найден:
     *     <ul>
     *       <li>Возвращает объект {@link UserDetails}</li>
     *       <li>Spring Security сравнивает пароли через {@link org.springframework.security.crypto.password.PasswordEncoder}</li>
     *     </ul>
     *   </li>
     *   <li>Если пользователь не найден:
     *     <ul>
     *       <li>Выбрасывает {@link UsernameNotFoundException}</li>
     *       <li>Spring Security прерывает аутентификацию</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param username имя пользователя для поиска
     * @return объект {@link UserDetails} с данными пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}