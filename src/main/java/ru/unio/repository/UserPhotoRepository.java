package ru.unio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import ru.unio.entity.UserPhoto;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с фотографиями пользователей.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Стандартные CRUD-операции (наследуются от {@link JpaRepository})</li>
 *   <li>Поиск фотографий по пользователю</li>
 *   <li>Управление главной фотографией профиля</li>
 *   <li>Пакетное удаление фотографий</li>
 * </ul>
 *
 * <h3>Особенности:</h3>
 * <ul>
 *   <li>Использует Spring Data JPA</li>
 *   <li>Автоматически генерирует реализации методов на основе соглашений об именовании</li>
 *   <li>Работает с сущностью {@link UserPhoto}</li>
 * </ul>
 */
@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {

    /**
     * Находит все фотографии пользователя.
     *
     * <h4>Эквивалентный SQL:</h4>
     * <pre>SELECT * FROM user_photos WHERE user_id = ?</pre>
     *
     * @param id идентификатор пользователя
     * @return список фотографий пользователя (может быть пустым)
     */
    List<UserPhoto> findByUserId(Long id);

    /**
     * Удаляет все фотографии пользователя.
     *
     * <h4>Эквивалентный SQL:</h4>
     * <pre>DELETE FROM user_photos WHERE user_id = ?</pre>
     *
     * @param id идентификатор пользователя
     */
    void deleteByUserId(Long id);

    /**
     * Находит главную фотографию пользователя.
     *
     * <h4>Эквивалентный SQL:</h4>
     * <pre>SELECT * FROM user_photos WHERE user_id = ? AND is_main = ?</pre>
     *
     * @param userId идентификатор пользователя
     * @param isMain флаг главной фотографии (true/false)
     * @return {@link Optional} с фотографией, если найдена
     */
    Optional<UserPhoto> findByUserIdAndIsMain(Long userId, boolean isMain);
}