package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "post_comment")
@Getter
@NoArgsConstructor
public class PostComment extends Timestamped{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_content")
    @Nullable
    private String content;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "user_id")
    @Nullable
    private Long userId;


    public PostComment(PostCommentRequestDto commentRequestDto, Post post, User user){
        this.content = commentRequestDto.getContent();
        this.userId = user.getId();
        this.post = post;
    }



}
