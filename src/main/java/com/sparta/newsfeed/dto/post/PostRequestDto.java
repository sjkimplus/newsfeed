package com.sparta.newsfeed.dto.post;

import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDto {
    private List<String> imageUrl;
    private String content;
}
