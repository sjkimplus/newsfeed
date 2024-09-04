package com.sparta.newsfeed.controller.post;

import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.service.PostService;
import lombok.RequiredArgsConstructor;
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
    @PostMapping(value = "/posts/{UserId}")
    public void createPost(@PathVariable("UserId") long userId,  @RequestPart("requestDto") PostRequestDto requestDto, @RequestPart("multipartFile") List<MultipartFile> multipartFile) {
        postService.createPost(userId, requestDto, multipartFile);
    }

    // 게시물 조회
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") long postId) {
        return postService.getPost(postId);
    }

    // 게시물 수정
    @PutMapping("/posts/{postId}")
    public void updatePost(@PathVariable("postId") long postId, @RequestPart("requestDto") PostRequestDto requestDto) {
        postService.updatePost(postId, requestDto);
    }

    // 게시물 삭제
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable("postId") long postId) {
        postService.deletePost(postId);
    }
    // 게시물 다건 조회

}