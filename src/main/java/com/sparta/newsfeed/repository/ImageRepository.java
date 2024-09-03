package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByTypeAndItemId(Type type, Long itemId);
}
