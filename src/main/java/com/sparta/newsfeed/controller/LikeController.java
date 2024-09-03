package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.like.LikeRequestDto;
import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/api/users/{user_id}/likes") // 좋아요 추가
    public LikeResponseDto addLike(@PathVariable Long user_id, @RequestBody LikeRequestDto requestDto) {
        return likeService.addLike(user_id, requestDto);
    }

    @DeleteMapping("/api/users/{user_id}/likes/delete") // 좋아요 취소
    public void deleteLike(@PathVariable Long user_id, @RequestBody LikeRequestDto requestDto) {
        likeService.deleteLike(user_id, requestDto);
    }
}
