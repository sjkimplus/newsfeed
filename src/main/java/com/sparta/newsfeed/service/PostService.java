package com.sparta.newsfeed.service;

import com.sparta.newsfeed.repository.PostRepository;

public class PostService {

    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
}