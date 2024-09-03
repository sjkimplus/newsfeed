package com.sparta.newsfeed.dto.comment;

import lombok.Getter;

@Getter
public class PostCommentRequestDto {

    private String content;
    private Long postId;
    private Long userId;
}
