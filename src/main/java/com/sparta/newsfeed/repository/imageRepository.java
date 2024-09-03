package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface imageRepository extends JpaRepository<Image, Long> {
}
