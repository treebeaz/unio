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
 * Конфигурация безопасности приложения с использованием Spring Security.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Настройка доступа к эндпоинтам</li>
 *   <li>Конфигурация процесса аутентификации</li>
 *   <li>Управление выходом из системы</li>
 *   <li>Интеграция с пользовательским сервисом аутентификации</li>
 *   <li>Настройка шифрования паролей</li>
 * </ul>
 *
 * <h3>Жизненный цикл аутентификации:</h3>
 * <ol>
 *   <li>Пользователь делает запрос к защищенному ресурсу</li>
 *   <li>Spring Security перехватывает запрос</li>
 *   <li>Для неаутентифицированных пользователей - перенаправление на /login</li>
 *   <li>После успешной аутентификации - перенаправление на /profile</li>
 *   <li>При выходе - очистка сессии и перенаправление на /?logout</li>
 * </ol>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Конфигурация цепочки фильтров безопасности.
     *
     * @param http объект для настройки безопасности
     * @param userDetailsService сервис для работы с пользователями
     * @return настроенная цепочка фильтров
     * @throws Exception при ошибках конфигурации
     *
     * <h4>Особенности конфигурации:</h4>
     * <ul>
     *   <li>Разрешен доступ без аутентификации к:
     *     <ul>
     *       <li>Главной странице ("/", "/home")</li>
     *       <li>Страницам входа и регистрации</li>
     *       <li>Статическим ресурсам (CSS, изображения)</li>
     *       <li>Странице создания профиля</li>
     *     </ul>
     *   </li>
     *   <li>Все остальные запросы требуют аутентификации и роли ROLE_USER</li>
     *   <li>Используется кастомная страница входа (/login)</li>
     *   <li>После успешного входа - перенаправление на /profile</li>
     *   <li>При выходе - перенаправление на главную страницу</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                // Интеграция с сервисом пользователей
                .userDetailsService(userDetailsService)

                // Настройка авторизации запросов
                .authorizeHttpRequests(auth -> auth
                        // Публичные маршруты
                        .requestMatchers("/", "/home", "/login", "/register", "/css/**",
                                "/images/**", "/profile/create", "/discover").permitAll()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().hasAuthority("ROLE_USER")
                )

                // Настройка формы входа
                .formLogin(form -> form
                        .loginPage("/login")               // Кастомная страница входа
                        .defaultSuccessUrl("/discover", true) // Перенаправление после входа
                        .permitAll()                       // Разрешить доступ к странице входа всем
                )

                // Настройка выхода
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")     // Перенаправление после выхода
                        .permitAll()                      // Разрешить выход всем аутентифицированным
                );

        return http.build();
    }

    /**
     * Бин для шифрования паролей.
     *
     * @return реализация PasswordEncoder на основе BCrypt
     *
     * <h4>Использование:</h4>
     * <ul>
     *   <li>При регистрации нового пользователя - кодирование пароля</li>
     *   <li>При аутентификации - проверка соответствия паролей</li>
     * </ul>
     *
     * <h4>Характеристики:</h4>
     * <ul>
     *   <li>Использует алгоритм BCrypt с солью</li>
     *   <li>Сила хеширования - 10 (по умолчанию)</li>
     *   <li>Автоматически генерирует случайную соль</li>
     * </ul>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}