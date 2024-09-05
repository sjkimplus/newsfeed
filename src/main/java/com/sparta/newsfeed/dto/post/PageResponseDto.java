package com.sparta.newsfeed.dto.post;

import com.sparta.newsfeed.entity.post.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PageResponseDto {
    private Long postId;
    private LocalDateTime createdDate;
    private List<String> imageUrl;

    public PageResponseDto(Post post, List<String> imageUrl) {
        this.postId = post.getId();
        this.createdDate = post.getCreatedDate();
        this.imageUrl = imageUrl;
    }

}
