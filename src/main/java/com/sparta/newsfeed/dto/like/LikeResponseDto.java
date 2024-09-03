package com.sparta.newsfeed.dto.like;

import com.sparta.newsfeed.entity.like.Like;
import lombok.Getter;

@Getter
public class LikeResponseDto {
    private final Long id;
    private final Long itemId;
    private final Long userId;

    public LikeResponseDto(Like like, Long userId) {
        this.id = like.getId();
        this.itemId = like.getItemId();
        this.userId = userId;
    }
}
