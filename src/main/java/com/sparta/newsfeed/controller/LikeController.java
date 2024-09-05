package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/likes") // 좋아요 추가
    public ResponseEntity<LikeResponseDto> addLike(
            @Auth AuthUser authUser,
            @RequestParam("type") LikeTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        return ResponseEntity.ok(likeService.addLike(authUser.getEmail(), type, itemId));
    }

    @DeleteMapping("/api/likes") // 좋아요 취소
    public String deleteLike(
            @Auth AuthUser authUser,
            @RequestParam("type") LikeTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        return likeService.deleteLike(authUser.getEmail(), type, itemId);
    }

    @GetMapping("/api/likes") // 좋아요 다건 조회
    public ResponseEntity<List<LikeResponseDto>> getLikes(
            @Auth AuthUser authUser,
            @RequestParam(name = "type", required = false) LikeTypeEnum type) {
        return ResponseEntity.ok(likeService.getLikes(authUser.getEmail(), type));
    }
}
