package com.sparta.newsfeed.dto.alarm;

import lombok.Getter;

@Getter
public class AlarmRequestDto {
    private Long id;
    private Boolean type;
    private Long itemId;
}