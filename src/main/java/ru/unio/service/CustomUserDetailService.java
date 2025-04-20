package ru.unio.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.unio.repository.UserRepository;

/**
 * <br>Сервис, реализующий интерфейс UserDetailsService.
 * <br>Этот интерфейс используется Spring Security для получения данных пользователя при аутентификации.
 *<br>
 * <br>Основные задачи:
 * <br>- Найти пользователя в базе данных по username
 * <br>- Вернуть объект UserDetails, содержащий учетные данные
 * <br>- В случае отсутствия пользователя — выбросить исключение UsernameNotFoundException
 *<br>
 * <br>Аннотация @Service:
 * <br>Обозначает, что данный класс является компонентом бизнес-логики (сервисом),
 * и Spring автоматически добавит его в контекст приложения.
 *<br>
 * <br>Связь с другими классами:
 * <br>- User (модель) реализует интерфейс UserDetails — в нем хранятся данные пользователя.
 * <br>- UserRepository — интерфейс, через который производится доступ к данным пользователя в БД.
 * <br>- Spring Security вызывает метод loadUserByUsername(), чтобы получить объект UserDetails
 *<br>
 *  <br>В связке с SecurityConfig, этот класс является мостом между логикой аутентификации и базой данных.
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
     * <br>Метод срабатывает при входе через форму:
     * <br>- Пользователь вводит логин/пароль -> отправляет POST запрос на /login
     * <br>- Spring Security:
     * <br>- Перехватывает запрос
     * <br>- Вызывает наш метод loadUserByUsername
     * <br>- Сравнивает пароль (используя автоматически PasswordEncoder в SecurityConfig.java)
     * <br>
     * <br> Если говорить коротко - ищется пользователь с таким же username.
     * <br> Если находит - возвращает его(UserDetails! НЕ User!)
     * <br> Если нет - выбрасывает исключение - такого пользователя не существует.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
