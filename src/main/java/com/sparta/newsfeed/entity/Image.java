package com.sparta.newsfeed.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "image")
@NoArgsConstructor
@Setter
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
    private LocalDateTime createdAt;


    public Image(Long itemId, Type type, List<String> imageUrl){
        this.itemId = itemId;
        this.type = type;
        this.imageUrl = imageUrl;
    }
    // URL을 추가하는 메서드
    public void addImageUrl(String url) {
        this.imageUrl.add(url);
    }
}
