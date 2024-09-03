package com.sparta.newsfeed.dto.comment;

import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.entity.PostComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostCommentResponseDto {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostCommentResponseDto(PostComment postComment, PostCommentRequestDto requestDto, Long postId) {
        this.id = postComment.getId();
        this.postId = postId;
        this.userId = requestDto.getUserId();
        this.content = postComment.getContent();
        this.createdAt = postComment.getCreateAt();
        this.updatedAt = postComment.getUpdatedAt();
    }

    public PostCommentResponseDto(PostComment postComment){
        this.id = postComment.getId();
        this.postId = postComment.getPost().getId();
        this.userId = postComment.getUserId();
        this.content = postComment.getContent();
        this.createdAt = postComment.getCreateAt();
        this.updatedAt = postComment.getUpdatedAt();
    }

}
