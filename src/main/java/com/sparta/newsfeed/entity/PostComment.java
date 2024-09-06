package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import com.sparta.newsfeed.entity.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "post_comment")
@Getter
@NoArgsConstructor
@Setter
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

    @Column(name = "user_name")
    @Nullable
    private String userName;


    public PostComment(PostCommentRequestDto commentRequestDto, Post post, User user){
        this.content = commentRequestDto.getContent();
        this.userId = commentRequestDto.getUserId();
        this.post = post;
        this.userName = user.getName();
    }

    public void commentsModify(String content) {
        this.content = content;
    }

}
