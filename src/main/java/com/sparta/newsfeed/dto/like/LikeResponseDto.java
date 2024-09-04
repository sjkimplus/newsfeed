package com.sparta.newsfeed.dto.like;

import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LikeResponseDto {
    private final Long id;
    private final Long userId;
    private final LikeTypeEnum type;
    private final Long itemId;
    private final LocalDateTime createAt;

    public LikeResponseDto(Like like) {
        this.id = like.getId();
        this.userId = like.getUser().getId();
        this.type = like.getType();
        this.itemId = like.getItemId();
        this.createAt = like.getCreateAt();
    }
}
