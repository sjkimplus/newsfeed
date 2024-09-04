package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
