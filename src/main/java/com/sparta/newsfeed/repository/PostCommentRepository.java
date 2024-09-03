package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
