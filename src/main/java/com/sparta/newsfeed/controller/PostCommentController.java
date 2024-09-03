package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import com.sparta.newsfeed.dto.comment.PostCommentResponseDto;
import com.sparta.newsfeed.service.PostCommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostCommentController {

    private final PostCommentService commentService;

    @PostMapping("/schedule/comment")
    public ResponseEntity<PostCommentResponseDto> createdComment(@RequestBody PostCommentRequestDto commentReqDto){
        return ResponseEntity.ok(commentService.createdComment(commentReqDto));
    }

    @GetMapping("/post/{post_id}/comment/{comment_id}")
    public ResponseEntity<PostCommentResponseDto> readComment(@PathVariable Long post_id, @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.readComment(post_id,comment_id));
    }

    @GetMapping("/post/comment")
    public ResponseEntity<List<PostCommentResponseDto>> findByAllComment(Long post_id, Long comment_id){
        return ResponseEntity.ok(commentService.findByAllComment(post_id,comment_id));
    }
}
