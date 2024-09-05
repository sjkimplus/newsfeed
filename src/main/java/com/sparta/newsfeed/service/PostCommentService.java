package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import com.sparta.newsfeed.dto.comment.PostCommentResponseDto;
import com.sparta.newsfeed.entity.post.Post;
import com.sparta.newsfeed.entity.PostComment;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.repository.AlarmRepository;
import com.sparta.newsfeed.repository.PostCommentRepository;
import com.sparta.newsfeed.repository.PostRepository;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {


    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    @Transactional // 댓글 작성
    public PostCommentResponseDto createdComment(PostCommentRequestDto commentReqDto, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );
        User user = userRepository.findById(commentReqDto.getUserId()).orElseThrow(() ->
                new EntityNotFoundException("사용자를 찾을 수 없습니다.")
        );

        PostComment postComment = new PostComment(commentReqDto, post);
        commentRepository.save(postComment);
        // 알림 추가
        // 자기 포스트에 자기가 댓글 제외
        if(!post.getUser().equals(user)) {
            sendAlarm(postComment.getId(), user);
        }

        return new PostCommentResponseDto(postComment, commentReqDto, postId);

    }


    // 특정 게시물에 있는 댓글 전체 조회
    public List<PostCommentResponseDto> findByAllComment(Long post_id) {
        List<PostComment> findByPostIdAll = commentRepository.findAllByPostId(post_id);

        List<PostCommentResponseDto> commentResponseDto = new ArrayList<>();
        for (PostComment postComment : findByPostIdAll) {
            PostCommentResponseDto postCommentResponseDto = new PostCommentResponseDto(postComment);
            commentResponseDto.add(postCommentResponseDto);
        }

        return commentResponseDto;
    }


    @Transactional
    public PostCommentResponseDto modifyComment(PostCommentRequestDto commentReqDto,
                                                Long postId, Long userId) {
        postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );
        PostComment comment = commentRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );
        comment.commentsModify(commentReqDto.getContent());
        return new PostCommentResponseDto(comment);
    }

    @Transactional
    public String deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("게시물을 찾을 수 없습니다.")
        );
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );
        if (post != null && comment != null) {
            commentRepository.deleteById(comment.getId());
            return comment.getId() + "번 댓글 삭제 완료";
        }else {
            return "게시물 또는 유저 아이디를 확인해주세요";
        }
    }

    // 알림 추가 메서드
    public void sendAlarm(Long itemId, User user) {
        // 유저 존재 확인
        Alarm alarm = new Alarm(AlarmTypeEnum.COMMENT, itemId, user);
        // 알림 저장
        alarmRepository.save(alarm);
    }
}
