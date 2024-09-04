package com.sparta.newsfeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    @Nullable
    private Long itemId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ElementCollection
    @Column(name = "image_url")
    private List<String> imageUrl = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    public Image(Long itemId, Type type, String imageUrl){
        this.itemId = itemId;
        this.type = type;
        this.imageUrl.add(imageUrl);
    }

    public void addImageUrl(String url) {
        this.imageUrl.add(url);
    }
}

