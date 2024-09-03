package com.sparta.newsfeed.entity.like;

import com.sparta.newsfeed.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "likes")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Like {
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

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    public Like(Boolean type, Long itemId, User user) {
        this.type = type ? LikeTypeEnum.POST : LikeTypeEnum.COMMENT;
        this.itemId = itemId;
        this.user = user;
    }
}
