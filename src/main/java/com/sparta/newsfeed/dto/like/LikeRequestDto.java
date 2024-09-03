package com.sparta.newsfeed.dto.like;

import lombok.Getter;

@Getter
public class LikeRequestDto {
    private Long id;
    private Boolean type;
    private Long itemId;
    private Long userId;
}
