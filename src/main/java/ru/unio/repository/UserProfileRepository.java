package ru.unio.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.unio.entity.User;
import ru.unio.entity.UserProfile;

import java.util.Optional;

/**
 * Репозиторий для работы с профилями пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Стандартные CRUD-операции (наследуются от {@link JpaRepository})</li>
 *   <li>Поиск профиля по пользователю</li>
 *   <li>Проверка существования профиля</li>
 *   <li>Пессимистичная блокировка профиля при изменении</li>
 * </ul>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Использует Spring Data JPA</li>
 *   <li>Содержит как производные методы, так и JPQL-запросы</li>
 *   <li>Поддерживает механизмы блокировок для конкурентного доступа</li>
 * </ul>
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Находит профиль пользователя.
     *
     * <h4>Эквивалентный SQL:</h4>
     * <pre>SELECT * FROM user_profile WHERE user_id = ?</pre>
     *
     * @param user сущность пользователя
     * @return {@link Optional} с профилем, если найден
     */
    Optional<UserProfile> findByUser(User user);

    /**
     * Проверяет существование профиля для пользователя.
     *
     * <h4>Эквивалентный SQL:</h4>
     * <pre>SELECT COUNT(*) > 0 FROM user_profile WHERE user_id = ?</pre>
     *
     * @param user сущность пользователя
     * @return true если профиль существует, иначе false
     */
    boolean existsByUser(User user);

    /**
     * Находит и блокирует профиль по имени пользователя с пессимистичной блокировкой.
     *
     * <h4>Особенности:</h4>
     * <ul>
     *   <li>Использует {@link LockModeType#PESSIMISTIC_WRITE} для предотвращения конкурентных изменений</li>
     *   <li>Выполняет JOIN с таблицей users</li>
     * </ul>
     *
     * <h4>JPQL запрос:</h4>
     * <pre>SELECT p FROM UserProfile p JOIN p.user u WHERE u.username = :username</pre>
     *
     * @param username имя пользователя
     * @return {@link Optional} с заблокированным профилем, если найден
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM UserProfile p JOIN p.user u WHERE u.username = :username")
    Optional<UserProfile> findAndLockByUsername(String username);
}