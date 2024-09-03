package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import com.sparta.newsfeed.dto.comment.PostCommentResponseDto;
import com.sparta.newsfeed.entity.Post;
import com.sparta.newsfeed.entity.PostComment;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.PostCommentRepository;
import com.sparta.newsfeed.repository.PostRepository;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Transactional
    public PostCommentResponseDto createdComment(PostCommentRequestDto commentReqDto) {
        Post post = postRepository.findById(commentReqDto.getPostId()).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );
        User user = userRepository.findById(commentReqDto.getUserId()).orElseThrow(() ->
                new EntityNotFoundException("사용자를 찾을 수 없습니다.")
        );

        PostComment postComment = new PostComment(commentReqDto, post, user);
        commentRepository.save(postComment);
        return new PostCommentResponseDto(postComment, commentReqDto);

    }

    public PostCommentResponseDto readComment(Long postId, Long commentId) {
        postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        PostComment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );

        return new PostCommentResponseDto(comment, postId);
    }

    public List<PostCommentResponseDto> findByAllComment(Long post_id, Long comment_id) {
        return null;
    }
}
