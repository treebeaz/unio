// LikeDto.java
package ru.unio.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {
    private Long id;
    private Long userId;
    private Long likedUserId;
    private LocalDateTime likedTime;

    public LikeDto(Long id, Long userId, Long likedUserId, LocalDateTime likedTime) {
        this.id = id;
        this.userId = userId;
        this.likedUserId = likedUserId;
        this.likedTime = likedTime;
    }
}