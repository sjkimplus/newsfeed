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
    public List<String> createPost(@PathVariable("id") long userId,  @RequestPart("requestDto") PostRequestDto requestDto, @RequestPart("multipartFile") List<MultipartFile> multipartFile) throws Exception {
        return postService.createPost(userId, requestDto, multipartFile);
    }

    // 게시물 조회
    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable("id") long postId) {
        return postService.getPost(postId);
    }

    // 게시물 수정


    // 게시물 삭제


    // 게시물

}