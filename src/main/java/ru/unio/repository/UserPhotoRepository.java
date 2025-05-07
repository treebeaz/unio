package ru.unio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.unio.entity.UserPhoto;

import java.util.List;
import java.util.Optional;

public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {

    List<UserPhoto> findByUserId(Long id);
    void deleteByUserId(Long id);
    Optional<UserPhoto> findByUserIdAndIsMain(Long userId, boolean isMain);
}
