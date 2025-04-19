package ru.unio.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.unio.entity.User;

import java.util.Optional;

/**
 * UserRepository - это интерфейс для работы с пользователями в базе данных. Он позволяет выполнять основные операции
 * (добавление, удаление, поиск) и некоторые специальные запросы.
 *
 * Последние два метода добавил чисто из-за того, что когда повторно пытался зарегестрироваться, хотя пользователь уже существовал, то id увеличивался
 * то есть когда был user_1 id= 1, пытался например создать еще одного такого с ником user_1, он выдавал ошибку, при этом при повторном создании уже нового пользователя, id был 3 -> user_2 id= 3
 *
 * Далее смотри UserService
 */

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Ищет пользователя по его логину (username).
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет, существует ли пользователь с таким логином, с блокировкой для записи (чтобы избежать одновременного создания одинаковых логинов).
     * Использует пессимистичную блокировку (не даст другим изменить запись, пока проверка не завершится)
     * Возвращает 1 если пользователь существует, или Optional.empty() если нет
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT 1 FROM User u WHERE u.username = :username")
    Optional<Integer> existsByUsername(String username);

    /**
     * Блокирует всю таблицу пользователей для записи. Используется для сложных операций, когда нужно гарантировать, что таблица не изменится во время выполнения.
     */
    @Modifying
    @Query(value = "LOCK TABLE users IN SHARE MODE", nativeQuery = true)
    void lockTableForWrite();
}

