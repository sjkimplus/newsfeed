package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.comment.PostCommentRequestDto;
import com.sparta.newsfeed.dto.comment.PostCommentResponseDto;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.post.Post;
import com.sparta.newsfeed.entity.PostComment;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.exception.CommentAuthOrVerificationException;
import com.sparta.newsfeed.exception.DataNotFoundException; // 사용된 예외 클래스
import com.sparta.newsfeed.exception.PasswordMismatchException;
import com.sparta.newsfeed.jwt.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Transactional // 댓글 작성
    public PostCommentResponseDto createdComment(String email, String tokenValue, PostCommentRequestDto commentReqDto, Long postId) {
        jwtUtil.checkAuth(tokenValue, email);

        // 게시물 찾기
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new DataNotFoundException("게시물을 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 사용자 찾기
        User user = userRepository.findById(commentReqDto.getUserId()).orElseThrow(() ->
                new DataNotFoundException("사용자를 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        PostComment postComment = new PostComment(commentReqDto, post, user);
        commentRepository.save(postComment);

        // 자기 포스트에 자기가 댓글 제외
        if (!post.getUser().equals(user)) {
            sendAlarm(postComment.getId(), post.getUser());
        }

        return new PostCommentResponseDto(postComment, commentReqDto, postId);
    }

    // 댓글 전체 조회
    public List<PostCommentResponseDto> findByAllComment(Long postId) {
        List<PostComment> comments = commentRepository.findAllByPostId(postId);

        if (comments.isEmpty()) {
            throw new DataNotFoundException("해당 게시물에 댓글이 없습니다."); // DataNotFoundException 사용
        }

        List<PostCommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (PostComment postComment : comments) {
            commentResponseDtos.add(new PostCommentResponseDto(postComment));
        }

        return commentResponseDtos;
    }

    @Transactional
    public PostCommentResponseDto modifyComment(String email, String tokenValue, PostCommentRequestDto commentReqDto,
                                                Long postId, Long commentId) {
        jwtUtil.checkAuth(tokenValue, email);

        // 게시물 찾기
        postRepository.findById(postId).orElseThrow(() ->
                new DataNotFoundException("게시물을 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 댓글 찾기
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new DataNotFoundException("댓글을 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 사용자 검증
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new DataNotFoundException("사용자를 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 댓글 작성자 검증
        if (!comment.getUserName().equals(user.getName())) {
            throw new CommentAuthOrVerificationException("댓글 작성자가 아닙니다."); // CommentAuthOrVerificationException 사용
        }

        // 댓글 수정
        comment.commentsModify(commentReqDto.getContent());
        return new PostCommentResponseDto(comment);
    }

    @Transactional
    public String deleteComment(String email, String tokenValue, Long postId, Long commentId) {
        jwtUtil.checkAuth(tokenValue, email);

        // 게시물 찾기
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new DataNotFoundException("게시물을 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 댓글 찾기
        PostComment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new DataNotFoundException("댓글을 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 사용자 검증
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new DataNotFoundException("사용자를 찾을 수 없습니다.")  // DataNotFoundException 사용
        );

        // 댓글 작성자 검증
        if (!comment.getUserName().equals(user.getName())) {
            throw new CommentAuthOrVerificationException("댓글 작성자가 아닙니다."); // CommentAuthOrVerificationException 사용
        }

        // 알림 삭제
        if(!postId.equals(user.getId())) {
            deleteAlarm(commentId);
        }
        // 댓글 삭제
        commentRepository.deleteById(comment.getId());
        return comment.getId() + "번 댓글 삭제 완료";
    }

    // 알림 추가 메서드
    public void sendAlarm(Long itemId, User user) {
        Alarm alarm = new Alarm(AlarmTypeEnum.COMMENT, itemId, user);
        alarmRepository.save(alarm);
    }
    // 알림 삭제 메서드
    private void deleteAlarm(Long commentId) {
        alarmRepository.delete(alarmRepository.findByTypeAndItemId(AlarmTypeEnum.COMMENT, commentId));
    }
}
