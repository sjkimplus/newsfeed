package com.sparta.newsfeed.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDto {
    private List<String> images;
    private String content;
}
