package ru.unio.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 *  Класс, представляющий сущность пользователя в системе.
 *  Реализует интерфейс UserDetails для интеграции с Spring Security.
 *
 *  Хранит учетные данные пользователя
 *  Обеспечивает аутентификацию через Spring Security
 *  Напрямую работает с таблицей users в Postgres
 *
 *  Короче говоря, тут лежит наша База Данных.
 *
 *  Интерфейс UserDetails - используется для того, чтобы понять, как выглядит и хранятся данные пользователя
 *  которые нужны для входа в систему(авторизации). Там еще функции есть, они нужны для обработки состояний нашего юзера,
 *  но они либо пустые, либо всегда тру, тк мы это пока не заполняем.
 *  Как я понял, этот интерфейс нужен для работы с Spring Security, так как он предоставляет всю информацию о пользователе.
 *
 *  Короче тут ооочень много писать про то,как работает Spring Security и UserDetails, но вот краткий ответ:
 *  Spring Security ожидает объект типа UserDetails для аутентификации. Без этого интерфейса он не знает, откуда брать
 *  username,password итд. Поэтому наш класс User имплементируется от этого интерфейса для предоставления информации.
 *
 *  Аннотации:
 *      @Entity - Указывает, что класс является JPA-сущностью и будет отображаться на таблицу в базе данных
 *      @Tables - Указывает, что эта сущность отображается на таблицу с именем "users" в БД
 *      @Id - Обозначает первичный ключ сущности
 *      @GeneratedValue - Указывает стратегию генерации значения первичного ключа — автоинкремент (IDENTITY) в Postgres
 *      @Column - Настройки колонки таблицы, nullable = not null, length = varchar(60) ...
 *
 *  Далее смотри файл CustomUserDetailService.java
 */

@Entity
@Table (name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 60)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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
