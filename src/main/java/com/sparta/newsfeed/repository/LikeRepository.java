package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like,Long> {
    Like findByTypeAndItemIdIn(String s, Long itemId);
}
