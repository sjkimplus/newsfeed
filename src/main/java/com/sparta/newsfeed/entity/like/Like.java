package com.sparta.newsfeed.entity.like;

import com.sparta.newsfeed.dto.like.LikeRequestDto;
import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.entity.PostComment;
import com.sparta.newsfeed.entity.Timestamped;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.alarm.Alarm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "like")
@NoArgsConstructor
public class Like extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @Enumerated(value = EnumType.STRING)
    private LikeTypeEnum type;
    @Column
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /* 긴가민가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postcomment_id")
    private PostComment postComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;
    */

    public Like(LikeRequestDto requestDto, User user) {
        if (requestDto.getType()) {
            this.type = LikeTypeEnum.POST;
        } else {
            this.type = LikeTypeEnum.COMMENT;
        }
        this.itemId = requestDto.getItemId();
        this.user = user;
    }
}
