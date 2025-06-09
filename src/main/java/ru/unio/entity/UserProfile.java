package ru.unio.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Сущность, представляющая профиль пользователя.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Хранение дополнительных данных о пользователе</li>
 *   <li>Связь один-к-одному с сущностью {@link User}</li>
 *   <li>Управление персональной информацией (возраст, город, био и др.)</li>
 * </ul>
 *
 * <h3>Особенности базы данных:</h3>
 * <ul>
 *   <li>Таблица: <code>user_profile</code></li>
 *   <li>Связь с таблицей <code>users</code> через <code>user_id</code></li>
 *   <li>Поле <code>age</code> вычисляемое (не обновляется напрямую)</li>
 * </ul>
 */
@Entity
@Table(name = "user_profile")
public class UserProfile {

    /**
     * Уникальный идентификатор профиля.
     * <p>
     * Стратегия генерации: {@link GenerationType#IDENTITY}
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Связанный пользователь (связь один-к-одному).
     * <p>
     * Особенности:
     * <ul>
     *   <li>Каскадные операции: ALL</li>
     *   <li>Связь через колонку <code>user_id</code></li>
     *   <li>Ограничение уникальности: true</li>
     * </ul>
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    /**
     * Дата рождения пользователя.
     * <p>
     * В базе данных хранится в колонке <code>birth_date</code>.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Возраст пользователя (вычисляемое поле).
     * <p>
     * Особенности:
     * <ul>
     *   <li>Не обновляется напрямую ({@code updatable = false})</li>
     *   <li>Не вставляется напрямую ({@code insertable = false})</li>
     *   <li>Должен вычисляться на основе {@code birthDate}</li>
     * </ul>
     */
    @Column(name = "age", insertable = false, updatable = false)
    private Integer age;

    /**
     * Пол пользователя.
     * <p>
     * В базе данных хранится в колонке <code>gender</code>.
     */
    @Column(name = "gender")
    private String gender;

    /**
     * Город проживания пользователя.
     * <p>
     * В базе данных хранится в колонке <code>city</code>.
     */
    @Column(name = "city")
    private String city;

    /**
     * Краткая биография пользователя.
     * <p>
     * В базе данных хранится в колонке <code>bio</code>.
     */
    @Column(name = "bio")
    private String bio;

    /**
     * URL основной фотографии профиля.
     * <p>
     * В базе данных хранится в колонке <code>photo_url</code>.
     */
    @Column(name = "photo_url")
    private String photoUrl;

    /**
     * Имя пользователя для отображения.
     * <p>
     * В базе данных хранится в колонке <code>name</code>.
     */
    @Column(name = "name")
    private String name;

    // ========== Геттеры и сеттеры ==========

    /**
     * Устанавливает связанного пользователя.
     * @param user сущность пользователя
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Устанавливает дату рождения.
     * @param birthDate новая дата рождения
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Устанавливает пол пользователя.
     * @param gender новый пол
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Устанавливает город проживания.
     * @param city новый город
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Устанавливает биографию пользователя.
     * @param bio новая биография
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Устанавливает URL фотографии профиля.
     * @param photoUrl новый URL фотографии
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Возвращает связанного пользователя.
     * @return сущность пользователя
     */
    public User getUser() {
        return user;
    }

    /**
     * Возвращает дату рождения.
     * @return дата рождения
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Возвращает возраст пользователя.
     * @return возраст (вычисляемое поле)
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Возвращает пол пользователя.
     * @return пол
     */
    public String getGender() {
        return gender;
    }

    /**
     * Возвращает город проживания.
     * @return город
     */
    public String getCity() {
        return city;
    }

    /**
     * Возвращает биографию пользователя.
     * @return биография
     */
    public String getBio() {
        return bio;
    }

    /**
     * Возвращает URL фотографии профиля.
     * @return URL фотографии
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Возвращает имя пользователя.
     * @return имя для отображения
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     * @param name новое имя
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}