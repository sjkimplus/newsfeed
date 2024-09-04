package com.sparta.newsfeed.controller.post;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    // 게시물 올리기
    @PostMapping(value = "/posts/{id}")
    public PostResponseDto createPost(@PathVariable("id") long userId,  @RequestPart("requestDto") PostRequestDto requestDto, @RequestPart("multipartFile") List<MultipartFile> multipartFile) throws Exception {
        return postService.createPost(userId, requestDto, multipartFile);
    }

    // 게시물 조회
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") long postId) {
        return postService.getPost(postId);
    }

    // 게시물 수정
    @PutMapping("/posts/{postId}")
    public void updatePost(@PathVariable("postId") long postId, @RequestParam("id") String content) {
        postService.updatePost(postId, content);
    }

    // 게시물 삭제


    // 게시물

}