package ru.unio.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unio.repository.UserRepository;

/**
 * Класс реализует интерфейс UserDetailService, который нужен Spring Security для получения информации о пользователе
 * при входе в систему
 *
 * Его основная задача - найти пользователя в базе по нику (username) и передать его данные в SpringSecurity для аутентификации
 *
 *  Аннотация @Service - используется для обозначения класса, который выполняет бизнес-логику приложения.
 *  Короче говоря, он говорит, что этот класс - сервис, который выполняет какие-то задачи, например обрабатывает данные или взаимодействует с бд
 *
 *  Этот интерфейс имеет один обязательный метод - loadUserByUsername - его вызывает Spring при логине.
 *
 *  Как связаны классы User и CustomUserDetailsService ?
 *  User - хранит данные пользователя, реализует UserDetails
 *  CustomUserDetailsService - загружает пользователя из базы по имени и возвращает объект UserDetails
 *  Spring security - вызывает loadUserByUsername(), получает User, проверяет пароль (Это происходит под капотом Spring Security)
 *
 *  Далее смотри файл SecurityConfig.java
 */

@Service
public class CustomUserDetailService implements UserDetailsService {
    /**
     * Репозиторий, через который осуществляется доступ к базе данных
     */
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Этот метод вызывается каждый раз, когда пользователь пытается войти в систему
     * Если пользователь не найден выбрасывается исключение.
     * @param username - логин, введеный пользователем при входе
     *
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));  // ищется пользователь в базе по логину, если не найден -> выбрасывает исключение
    }
}
