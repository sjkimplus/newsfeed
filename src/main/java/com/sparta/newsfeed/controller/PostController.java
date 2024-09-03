package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.PostRequestDto;
import com.sparta.newsfeed.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    // 게시물 올리기
//    @PostMapping("/posts/{id}")
//    public void createPost(@PathVariable("id") long userId, @RequestBody PostRequestDto requestDto) {
//        postService.createPost(userId, requestDto);
//    }

}