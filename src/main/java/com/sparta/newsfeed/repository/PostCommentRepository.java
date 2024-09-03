package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findAllByPostId(Long post_id);
}
