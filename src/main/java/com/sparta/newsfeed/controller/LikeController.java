package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/likes") // 좋아요 추가
    public LikeResponseDto addLike(
            @RequestParam("userId") Long userId,
            @RequestParam("type") LikeTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        return likeService.addLike(userId, type, itemId);
    }

    @DeleteMapping("/api/likes") // 좋아요 취소
    public void deleteLike(
            @RequestParam("userId") Long userId,
            @RequestParam("type") LikeTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        likeService.deleteLike(userId, type, itemId);
    }

    @GetMapping("/api/likes") // 좋아요 다건 조회
    public List<LikeResponseDto> getLikes(
            @RequestParam("userId") Long userId,
            @RequestParam(name = "type", required = false) LikeTypeEnum type) {
        return likeService.getLikes(userId, type);
    }
}
