package com.sparta.newsfeed.controller.post;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.dto.post.PostRequestDto;
import com.sparta.newsfeed.dto.post.PostResponseDto;
import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;
    private final JwtUtil jwtUtil;
    // 게시물 올리기
    @PostMapping(value = "/posts")
    public PostResponseDto createPost(@Auth AuthUser authUser,
                           @RequestPart("requestDto") PostRequestDto requestDto,
                           @RequestPart("multipartFile") List<MultipartFile> multipartFile) throws Exception {
        return postService.createPost(authUser.getEmail(), requestDto, multipartFile);


    // 게시물 조회
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                   @PathVariable("postId") long postId) {

        // 본인의 게시물 및 친구의 게시물인지 확인
        PostResponseDto postResponseDto = postService.getPost(postId);
        // 조회하려는 게시물이 본인의 게시물인지 확인
        jwtUtil.checkAuth(tokenValue, postResponseDto.getEmail());

        return postService.getPost(postId);
    }

    // 게시물 수정, 본인확인
    @PutMapping("/posts/{postId}")
    public void updatePost(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                           @PathVariable("postId") long postId,
                           @RequestPart("requestDto") PostRequestDto requestDto) {

        PostResponseDto postResponseDto = postService.getPost(postId);
        // 수정하려는 게시물의 글쓴이와 현제 수정요청을 하는 유저가 동일한지 확인
        jwtUtil.checkAuth(tokenValue, postResponseDto.getEmail());
        postService.updatePost(postId, requestDto);
    }

    // 게시물 삭제, 본인확인
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                           @PathVariable("postId") long postId) {

        PostResponseDto postResponseDto = postService.getPost(postId);
        // 수정하려는 게시물의 글쓴이와 현제 삭제요청을 하는 유저가 동일한지 확인
        jwtUtil.checkAuth(tokenValue, postResponseDto.getEmail());
        postService.deletePost(postId);
    }

    // 게시물 다건 조회



    // 뉴스피드

}