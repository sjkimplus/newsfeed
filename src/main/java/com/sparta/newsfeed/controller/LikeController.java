package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.dto.like.LikeResponseDto;
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
            @Auth AuthUser authUser,
            @RequestParam("type") LikeTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        return likeService.addLike(authUser.getEmail(), type, itemId);
    }

    @DeleteMapping("/api/likes") // 좋아요 취소
    public void deleteLike(
            @Auth AuthUser authUser,
            @RequestParam("type") LikeTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        likeService.deleteLike(authUser.getEmail(), type, itemId);
    }

    @GetMapping("/api/likes") // 좋아요 다건 조회
    public List<LikeResponseDto> getLikes(
            @Auth AuthUser authUser,
            @RequestParam(name = "type", required = false) LikeTypeEnum type) {
        return likeService.getLikes(authUser.getEmail(), type);
    }
}
