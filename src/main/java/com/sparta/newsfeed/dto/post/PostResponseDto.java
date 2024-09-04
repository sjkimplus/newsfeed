package com.sparta.newsfeed.dto.post;

import com.sparta.newsfeed.entity.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDto {
    private String content;
    private List<String> imageUrl;

//    private Long likeCount;

    public PostResponseDto(Post post, List<String> file) {
        this.imageUrl = file;
        this.content = post.getContent();
    }

}
