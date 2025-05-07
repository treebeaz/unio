package ru.unio.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.relation.Role;
import java.util.Collection;
import java.util.List;

/**
 * Класс, представляющий пользователя в системе.
 * Имплементирует интерфейс UserDetails для интеграции c SpringSecurity.
 *<br>
 * <br>Основные функции:
 * <br>Хранение учетных данных пользователя (username, password)
 * <br>Обеспечение аутентификации в системе
 * <br>Связь с таблицей "users" в базе данных Postgres
 * <br>Связь с профилем пользователя (UserProfile) через OneToOnes
 *<br>
 * <br>О Spring Security и UserDetails:<br>
 * Spring Security требует реализацию интерфейса UserDetails, чтобы понимать,
 * как получить данные пользователя для входа: имя, пароль, роли и статус аккаунта.
 * Без этой реализации система не сможет аутентифицировать пользователей.
 *<br>
 * <br>Методы вроде isAccountNonExpired() возвращают статус аккаунта. Сейчас они все возвращают true,
 * так как мы не реализуем логику блокировки или деактивации учетных записей.
 *<br>
 * <br>Аннотации:
 * @Entity - помечает класс как JPA-сущность (таблица в БД)
 * @Tables(name = "users") — задаёт имя таблицы в базе
 * @Id - первичный ключ
 * @GeneratedValue - автоинкремент (стратегия IDENTITY для Postgres)
 * @Column - задаёт параметры поля в таблице: уникальность, длина, nullability
 *
 */

@Entity
@Table (name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPhoto> photos;

    @Column(unique = true, nullable = false, length = 60)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Transient
    private int countPhotos;

    {
        countPhotos = 0;
    }

    public int getCountPhotos() {
        return countPhotos;
    }

    public void setCountPhotos(int countPhotos) {
        this.countPhotos = countPhotos;
    }

    public int changeCountPhotos() {
        return ++countPhotos;
    }

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

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
