package com.sparta.newsfeed.repository;

import com.sparta.newsfeed.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUserEmail(String userEmail, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.createdDate DESC")
    List<Post> findPostsByCreatedDateDesc();

    @Query("SELECT p FROM Post p LEFT JOIN Like l ON p.id = l.itemId AND l.type = 'POST' " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(l) DESC")
    List<Post> findPostsByLikeCount();


}
