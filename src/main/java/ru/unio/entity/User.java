package ru.unio.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import java.util.Collection;
import java.util.List;

/**
 * Класс-сущность, представляющий пользователя системы.
 * Реализует интерфейс {@link UserDetails} для интеграции с Spring Security.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Хранение учетных данных пользователя (логин и пароль)</li>
 *   <li>Обеспечение механизма аутентификации через Spring Security</li>
 *   <li>Связь с профилем пользователя ({@link UserProfile})</li>
 *   <li>Управление фотографиями пользователя ({@link UserPhoto})</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Таблица в БД: <code>users</code></li>
 *   <li>Используется стратегия генерации ID: <code>IDENTITY</code></li>
 *   <li>Все пользователи имеют роль <code>ROLE_USER</code></li>
 *   <li>Временное поле <code>countPhotos</code> (не сохраняется в БД)</li>
 * </ul>
 *
 * <h3>Spring Security Integration:</h3>
 * <p>
 * Реализация интерфейса {@link UserDetails} необходима для работы с Spring Security.
 * Методы интерфейса предоставляют информацию о:
 * </p>
 * <ul>
 *   <li>Учетных данных (логин/пароль)</li>
 *   <li>Ролях пользователя</li>
 *   <li>Статусе учетной записи</li>
 * </ul>
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя.
     * <p>
     * Стратегия генерации: <code>GenerationType.IDENTITY</code>
     * (автоинкремент для PostgreSQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Профиль пользователя (связь один-к-одному).
     * <p>
     * Каскадные операции:
     * <ul>
     *   <li>ALL - все операции каскадируются</li>
     *   <li>orphanRemoval - удаление профиля при удалении пользователя</li>
     * </ul>
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    /**
     * Фотографии пользователя (связь один-ко-многим).
     * <p>
     * Каскадные операции:
     * <ul>
     *   <li>ALL - все операции каскадируются</li>
     *   <li>orphanRemoval - удаление фотографий при удалении пользователя</li>
     * </ul>
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPhoto> photos;

    /**
     * Логин пользователя.
     * <p>
     * Ограничения в БД:
     * <ul>
     *   <li>Уникальное значение</li>
     *   <li>Не может быть null</li>
     *   <li>Максимальная длина: 60 символов</li>
     * </ul>
     */
    @Column(unique = true, nullable = false, length = 60)
    private String username;

    /**
     * Пароль пользователя (в зашифрованном виде).
     * <p>
     * Ограничения в БД:
     * <ul>
     *   <li>Не может быть null</li>
     *   <li>Максимальная длина: 100 символов</li>
     * </ul>
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * Счетчик фотографий (временное поле, не сохраняется в БД).
     * <p>
     * Используется для временных операций в бизнес-логике.
     */
    @Transient
    private int countPhotos = 0;

    /**
     * Получает количество фотографий.
     * @return текущее количество фотографий
     */
    public int getCountPhotos() {
        return countPhotos;
    }

    /**
     * Устанавливает количество фотографий.
     * @param countPhotos новое значение счетчика
     */
    public void setCountPhotos(int countPhotos) {
        this.countPhotos = countPhotos;
    }

    /**
     * Увеличивает счетчик фотографий на 1.
     * @return новое значение счетчика
     */
    public int changeCountPhotos() {
        return ++countPhotos;
    }

    // Стандартные геттеры и сеттеры

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Возвращает список ролей пользователя.
     * <p>
     * В текущей реализации все пользователи имеют роль <code>ROLE_USER</code>.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Методы статуса учетной записи (всегда активны в текущей реализации)

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}