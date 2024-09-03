package com.sparta.newsfeed.dto.like;

import lombok.Getter;

@Getter
public class LikeRequestDto {
    private Long id;
    private Long userId;
    private Boolean type;
    private Long itemId;
}
