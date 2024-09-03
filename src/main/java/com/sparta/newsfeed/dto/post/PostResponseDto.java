package com.sparta.newsfeed.dto.post;

import com.sparta.newsfeed.dto.comment.CommentResponseDto;
import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.entity.PostComment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponseDto {
    private List<String> imageUrl;
    private String content;
    private Long likeCount;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Post post, List<String> imageUrl, Long likeCount, List<PostComment> postComments) {
        this.imageUrl = imageUrl;
        this.content = post.getContent();
        this.likeCount = likeCount;
        this.comments = new ArrayList<>();

        // parse comments as Dto
        for (PostComment postComment : postComments) {
            comments.add(new CommentResponseDto(postComment));
        }
    }
}
