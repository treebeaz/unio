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
 * Класс использует Spring Security для обеспечения аутентификации и защиты маршрутов. Вся логика построена на собственный реализациях:
 * 1. Пользовательская сущность User реализует UserDetails
 * 2. Кастомный сервис CustomUserDetailService реализуется UserDetailService
 * 3. Настройки безопасности определяются тут. (в SecurityConfig)
 * Он определяет, как пользователи могут входить в систему, какие страницы доступны без входа и как обрабатываются выходы из системы (еще не реализовано )))
 *
 * Короче говоря, этот класс управляет всей конфигурацией безопасности.
 * То есть, он говорит Spring Security: какие страницы открыты всем, как обрабатывать логин и логаут, как загружать пользователе, как проверять пароли
 *
 * Как связаны User CustomUserDetailService SecurityConfig:
 * 1. User - модель пользователя
 * 2. Custom... - Загружает пользователя из БД по логину(userDetailService)
 * 3. SecurityConfig - настравивает - кого пускать, как логиниться, как сравнивать пароли
 * 4. PasswordEncoder - используется для шифровки и проверки паролей(BCrypt)
 *
 * Аннотации:
 * @Configuration - помечается для класса-конфигурации Spring
 * @EnableWebSecurity - включает механизм Spring Security
 * @Bean - используется для опеределения метода, который создает и настраивает Объект. Spring берет на себя управление этим объектом. (не знаю для чего )))
 *
 *
 * Как работает:
 * Пользователь отправляет форму входа /login с username и password
 *
 * Spring Security перехватывает форму
 *
 * Вызывает loadUserByUsername() у CustomUserDetailService
 *
 * Из БД достаётся объект User
 *
 * Пароль проверяется с использованием PasswordEncoder.matches(...)
 *
 * Если всё совпало — пользователь авторизован, данные сохраняются в SecurityContext
 *
 * Далее смотри UserRepository
 *
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/login", "/register", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
