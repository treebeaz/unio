package ru.unio.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.unio.entity.User;

import java.util.Optional;

/**
 * <br>Интерфейс для доступа к данным пользователей в базе данных.
 * <br>Расширяет JpaRepository, предоставляя базовые CRUD-операции без дополнительного кода.
 * <br>
 * <br>Основные возможности:
 * <br>- findByUsername: найти пользователя по логину
 * <br>- existsByUsername: проверить наличие пользователя с блокировкой
 * <br>- lockTableForWrite: заблокировать таблицу для инкрементирования id
 * <br>
 * <br>Почему он важен:
 * <br> Репозиторий инкапсулирует работу с базой данных, и позволяет использовать мощь Spring Data JPA:
 * <br>- Без написания SQL можно находить, сохранять, обновлять данные
 * <br>- Можно использовать JPQL или нативный SQL при необходимости
 * <br>
 * <br>Почему добавлены ручные блокировки:
 * <br>При попытке зарегистрировать уже существующего пользователя происходила ошибка, но ID в таблице продолжал инкрементироваться.
 *    Это значит, что транзакция была отклонена, но auto-increment сработал — в Postgres это поведение по умолчанию.
 * <br>
 * <br>Чтобы избежать коллизий и гонок при регистрации:
 * <br>existsByUsername использует пессимистичную блокировку (LockModeType.PESSIMISTIC_WRITE),
 *  которая гарантирует, что никто не сможет изменить/создать запись с этим username до окончания текущей операции.
 * <br>
 * <br>Используется в:
 * <br>-CustomUserDetailService — при логине
 * <br>-UserService — при регистрации и проверке логинов
 * <br>
 * <br>Аннотация:
 * <br>@Service - используется для обозначения класса, который выполняет бизнес-логику приложения.
 * <br>
 *  Этот интерфейс имеет один обязательный метод - loadUserByUsername - его вызывает Spring при логине.
 *
 */

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по уникальному логину (username).
     * <p>
     * Автоматически генерируемый Spring Data JPA запрос. Эквивалентен SQL:
     * {@code SELECT * FROM users WHERE username = ? LIMIT 1}
     *
     * @param username Логин пользователя
     * @return Optional с найденным пользователем или empty, если не существует
     * @implNote Использует индекс по полю username для оптимальной производительности
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверить существование пользователя с блокировкой.
     *
     * Используется при регистрации, чтобы два человека
     * не смогли зарегистрировать одинаковые логины одновременно.
     *
     * @param username Проверяемый логин
     * @return 1 если пользователь существует, иначе empty
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT 1 FROM User u WHERE u.username = :username")
    Optional<Integer> existsByUsername(String username);

    /**
     * Заблокировать таблицу пользователей.
     *
     * Используется ТОЛЬКО для особых операций,
     * когда нужно гарантировать целостность данных.
     *
     */
    @Modifying
    @Query(value = "LOCK TABLE users IN SHARE MODE", nativeQuery = true)
    void lockTableForWrite();
}

