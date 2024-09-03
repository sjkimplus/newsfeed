package com.sparta.newsfeed.controller.post;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
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

    // 게시물 조회
    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable("id") long postId) {
        PostResponseDto responseDto = postService.getPost(postId);
        return responseDto;
    }

    // 게시물 수정


    // 게시물 삭제


    // 게시물

}