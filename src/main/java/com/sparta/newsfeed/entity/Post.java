package com.sparta.newsfeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 id 외래키
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "date_modified")
    private LocalDateTime modifiedDate;

    public Post(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public void updatePost(String text) {
        this.content = text;
    }
}
