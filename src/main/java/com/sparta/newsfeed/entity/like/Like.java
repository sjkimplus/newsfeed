package com.sparta.newsfeed.entity.like;

import com.sparta.newsfeed.dto.like.LikeRequestDto;
import com.sparta.newsfeed.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "like")
@NoArgsConstructor
public class Like {
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
