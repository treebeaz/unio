package ru.unio.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.unio.entity.User;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями в системе.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Стандартные CRUD-операции (наследуются от {@link JpaRepository})</li>
 *   <li>Поиск пользователей по логину</li>
 *   <li>Проверка уникальности логина с блокировками</li>
 *   <li>Управление блокировками таблицы</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Использует Spring Data JPA с поддержкой как производных методов, так и JPQL/Native SQL</li>
 *   <li>Реализует механизмы пессимистичных блокировок для конкурентного доступа</li>
 *   <li>Содержит специальные методы для работы с последовательностями ID в PostgreSQL</li>
 * </ul>
 *
 * <h3>Использование в системе:</h3>
 * <ul>
 *   <li>{@link ru.unio.service.CustomUserDetailService} - при аутентификации</li>
 *   <li>{@link ru.unio.service.UserService} - при регистрации и управлении пользователями</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по логину.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Использует индекс по полю username</li>
     *   <li>Возвращает {@link Optional} для безопасной обработки отсутствия пользователя</li>
     * </ul>
     *
     * <h4>Эквивалентный SQL:</h4>
     * <pre>SELECT * FROM users WHERE username = ? LIMIT 1</pre>
     *
     * @param username логин пользователя (уникальный)
     * @return {@link Optional} с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет существование пользователя с пессимистичной блокировкой.
     *
     * <h4>Назначение:</h4>
     * <p>
     * Предотвращает race condition при регистрации пользователей,
     * гарантируя что только одна транзакция может проверить/создать
     * пользователя с конкретным логином.
     * </p>
     *
     * <h4>Технические детали:</h4>
     * <ul>
     *   <li>Использует {@link LockModeType#PESSIMISTIC_WRITE}</li>
     *   <li>Возвращает 1 если пользователь существует (не загружая сущность)</li>
     *   <li>Оптимизирован для минимального блокирующего воздействия</li>
     * </ul>
     *
     * <h4>JPQL запрос:</h4>
     * <pre>SELECT 1 FROM User u WHERE u.username = :username</pre>
     *
     * @param username проверяемый логин
     * @return {@link Optional} с 1 если пользователь существует
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT 1 FROM User u WHERE u.username = :username")
    Optional<Integer> existsByUsername(String username);

    /**
     * Блокирует таблицу пользователей в режиме SHARE.
     *
     * <h4>Назначение:</h4>
     * <p>
     * Используется для специальных операций, требующих гарантированной
     * целостности последовательности ID, особенно в PostgreSQL.
     * </p>
     *
     * <h4>Особенности использования:</h4>
     * <ul>
     *   <li>Должен вызываться в отдельной транзакции</li>
     *   <li>Блокирует всю таблицу - использовать с осторожностью</li>
     *   <li>Необходим для работы вокруг особенностей PostgreSQL с sequences</li>
     * </ul>
     *
     * <h4>Native SQL запрос:</h4>
     * <pre>LOCK TABLE users IN SHARE MODE</pre>
     */
    @Modifying
    @Query(value = "LOCK TABLE users IN SHARE MODE", nativeQuery = true)
    void lockTableForWrite();
}

