package ru.unio.entity;

import jakarta.persistence.*;
/**
 * Сущность, представляющая фотографию пользователя.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Хранение информации о фотографиях пользователей</li>
 *   <li>Поддержка главной фотографии профиля</li>
 *   <li>Связь с сущностью {@link User}</li>
 * </ul>
 *
 * <h3>Схема базы данных:</h3>
 * <ul>
 *   <li>Таблица: <code>user_photos</code></li>
 *   <li>Связь многие-к-одному с таблицей <code>users</code></li>
 * </ul>
 */
@Entity
@Table(name = "user_photos")
public class UserPhoto {

    /**
     * Уникальный идентификатор фотографии.
     * <p>
     * Стратегия генерации: {@link GenerationType#IDENTITY}
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, которому принадлежит фотография.
     * <p>
     * Связь многие-к-одному с сущностью {@link User}.
     * В базе данных представлена колонкой <code>user_id</code> с ограничением NOT NULL.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * URL или путь к файлу фотографии.
     * <p>
     * Ограничения:
     * <ul>
     *   <li>Максимальная длина: 255 символов</li>
     * </ul>
     */
    @Column(length = 255)
    private String photoUrl;

    /**
     * Флаг, указывающий является ли фотография главной для профиля.
     * <p>
     * Особенности:
     * <ul>
     *   <li>По умолчанию: false</li>
     *   <li>Только одна фотография может быть главной для каждого пользователя</li>
     * </ul>
     */
    @Column()
    private boolean isMain;

    /**
     * Возвращает идентификатор фотографии.
     * @return уникальный идентификатор
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает пользователя, которому принадлежит фотография.
     * @return сущность пользователя
     */
    public User getUser() {
        return user;
    }

    /**
     * Устанавливает пользователя для фотографии.
     * @param user сущность пользователя
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Возвращает путь к фотографии.
     * @return URL или путь к файлу
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Устанавливает путь к фотографии.
     * @param photoUrl новый URL или путь к файлу
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Проверяет, является ли фотография главной.
     * @return true если фотография главная, иначе false
     */
    public boolean isMain() {
        return isMain;
    }

    /**
     * Устанавливает статус фотографии как главной.
     * @param main true чтобы сделать фотографию главной
     */
    public void setMain(boolean main) {
        isMain = main;
    }
}