package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    //    Like findByTypeAndItemIdIn(String type, Long itemId);
    long countByTypeAndItemId(LikeTypeEnum type, Long itemId);

    Like findByTypeAndItemId(LikeTypeEnum type, Long itemId);

    List<Like> findAllByUserOrderByCreateAtDesc(User user);

    List<Like> findAllByUserAndTypeOrderByCreateAtDesc(User user, LikeTypeEnum type);

    List<Like> findAllByUserAndTypeAndItemIdOrderByCreateAtDesc(User user, LikeTypeEnum type, Long itemId);
}
