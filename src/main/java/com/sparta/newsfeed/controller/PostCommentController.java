package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import com.sparta.newsfeed.dto.comment.PostCommentResponseDto;
import com.sparta.newsfeed.jwt.JwtUtil;
import com.sparta.newsfeed.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostCommentController {

    private final PostCommentService commentService;


    @PostMapping("/posts/{postId}/comments")    // 댓글 작성
    public ResponseEntity<PostCommentResponseDto> createdComment(@Auth AuthUser authUser, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                                                 @RequestBody PostCommentRequestDto commentReqDto,
                                                                 @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.createdComment(authUser.getEmail(), tokenValue, commentReqDto, postId));
    }

    @GetMapping("/posts/{postId}/comments")    //특정 게시물에 있는 댓글 전체 조회
    public ResponseEntity<List<PostCommentResponseDto>> findByAllComment(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByAllComment(postId));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")    // 특정 게시물 수정
    public ResponseEntity<PostCommentResponseDto> modifyComment(@Auth AuthUser authUser, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                                                @RequestBody PostCommentRequestDto commentReqDto,
                                                                @PathVariable Long postId,
                                                                @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.modifyComment(authUser.getEmail(), tokenValue, commentReqDto, postId, commentId));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(@Auth AuthUser authUser, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                        @PathVariable Long postId,
                                        @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(authUser.getEmail(), tokenValue, postId, commentId));
    }
}
