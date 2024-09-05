package com.sparta.newsfeed.dto.post;

import com.sparta.newsfeed.dto.comment.CommentResponseDto;
import com.sparta.newsfeed.entity.post.Post;
import com.sparta.newsfeed.entity.PostComment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponseDto {
    private String email;
    private String content;
    private List<String> imageUrl;
    private Long likeCount;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Post post, List<String> file, Long likeCount, List<PostComment> comments) {
        this.email = post.getUser().getEmail();
        this.imageUrl = file;
        this.content = post.getContent();
        this.likeCount = likeCount;
        this.comments = new ArrayList<>();

        for (PostComment postComment : comments) {
            this.comments.add(new CommentResponseDto(postComment));
        }
    }

}
