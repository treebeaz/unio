package ru.unio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <br>Конфигурационный класс, отвечающий за настройку безопасности приложения с использованием Spring Security.
 *<br>
 * <br>Основные задачи:
 * <br>- Настраивает, какие маршруты доступны без авторизации
 * <br>- Обрабатывает формы логина и логаута (еще не реализовано)
 * <br>- Подключает кастомный сервис пользователей (CustomUserDetailService)
 * <br>- Настраивает шифрование паролей через BCryptPasswordEncoder
 *<br>
 * <br>Связанные компоненты:
 * <br>1. User — модель пользователя, реализует интерфейс UserDetails (для передачи данных Spring Security)
 * <br>2. CustomUserDetailService — сервис, реализующий интерфейс UserDetailsService, загружает пользователя из БД
 * <br>3. SecurityConfig — текущий класс, который говорит Spring Security, как именно настраивать безопасность
 * <br>4. PasswordEncoder — используется для шифрования и проверки паролей при логине
 *<br>
 * <br>Аннотации:
 * <br>- @Configuration — помечает класс как конфигурационный, Spring будет использовать его при запуске
 * <br>- @EnableWebSecurity — активирует систему безопасности Spring Security
 * <br>- @Bean — указывает, что метод создает и настраивает объект, которым будет управлять Spring (НЕ мы!)
 *<br>
 * <br>Алгоритм работы логина:
 * <br>1. Пользователь отправляет POST-запрос на `/login` с логином и паролем
 * <br>2. Spring Security перехватывает запрос
 * <br>3. Вызывает метод `loadUserByUsername()` из CustomUserDetailService
 * <br>4. Загружается пользователь из базы
 * <br>5. Пароль проверяется через PasswordEncoder (BCrypt)
 * <br>6. При успешной проверке — пользователь сохраняется в SecurityContext и считается аутентифицированным
 *<br>
 * <br>Что происходит дальше:
 * <br>- При успешной аутентификации пользователя перенаправляют на `/profile` (.defaultSuccessUrl("/profile", true))
 *<br>
 * <br>Примечания:
 * <br>- Все маршруты, кроме указанных явно в `.permitAll()`, требуют авторизации и роли `ROLE_USER`
 *
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Настройка цепочки фильтров безопасности. Определяет:
     * - какие страницы доступны
     * - как осуществляется вход в систему
     * - как происходит выход
     * - как обрабатываются пользователи и пароли
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                // Сервис для загрузки пользователя из БД
                .userDetailsService(userDetailsService)

                // Разрешения на доступ к маршрутам
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login", "/register", "/css/**", "/profile/create").permitAll()
                        .anyRequest().hasAuthority("ROLE_USER")  // остальные маршруты — только для авторизованных
                )

                // Настройка формы логина
                .formLogin(form -> form
                        .loginPage("/login")  // пользовательская страница входа
                        .defaultSuccessUrl("/profile", true)  // после успешного входа — на страницу профиля
                        .permitAll()
                )

                // Настройка выхода из системы
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")   // после выхода — на главную
                        .permitAll()
                );

        return http.build();  // возвращаем настроенную цепочку фильтров
    }

    /**
     * Компонент шифрования паролей.
     * Используется при:
     *  - Сравнении пароля при входе (Spring сам вызывает .matches(...))
     *  - Шифровании пароля при регистрации (UserService → passwordEncoder.encode(...))
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
