package com.sparta.newsfeed.dto.comment;

import com.sparta.newsfeed.entity.PostComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private String name;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(PostComment comment) {
        this.name = comment.getUserName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreateAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
