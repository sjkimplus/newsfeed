package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByTypeAndItemId (Type type, Long itemId);

}
