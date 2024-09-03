package com.sparta.newsfeed.entity.like;

import com.sparta.newsfeed.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "likes")
@NoArgsConstructor
public class Like{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LikeTypeEnum type;

    @Column(name = "itemId", nullable = false)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Like(Boolean type, Long itemId, User user) {
        if (type) {
            this.type = LikeTypeEnum.POST;
        } else {
            this.type = LikeTypeEnum.COMMENT;
        }
        this.itemId = itemId;
        this.user = user;
    }
}
