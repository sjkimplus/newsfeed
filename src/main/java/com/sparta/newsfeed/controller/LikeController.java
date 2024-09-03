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

    @GetMapping("/api/likes") // 좋아요 추가
    public LikeResponseDto addLike(
            @RequestParam("userId") Long userId,
            @RequestParam("type") Boolean type,
            @RequestParam("itemId") Long itemId) {
        return likeService.addLike(userId, type, itemId);
    }

    @DeleteMapping("/api/likes") // 좋아요 취소
    public void deleteLike(
            @RequestParam("userId") Long userId,
            @RequestParam("type") Boolean type,
            @RequestParam("itemId") Long itemId) {
        likeService.deleteLike(userId, type, itemId);
    }
}
