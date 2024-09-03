package com.sparta.newsfeed.dto.post;

import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDto {
    private List<String> imageUrl;
    private String content;
//    private Long likeCount;

    public PostResponseDto(Post post, List<String> imageUrl) {
        this.imageUrl = imageUrl;
        this.content = post.getContent();
    }

}
