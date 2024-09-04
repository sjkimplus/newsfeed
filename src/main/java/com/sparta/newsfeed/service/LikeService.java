package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.LikeRepository;
import com.sparta.newsfeed.repository.PostCommentRepository;
import com.sparta.newsfeed.repository.PostRepository;
import com.sparta.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Transactional
    public LikeResponseDto addLike(String userEmail, LikeTypeEnum type, Long itemId) {
        // 유저 존재 확인
        User user = findUserEmail(userEmail);
        // type itemId 존재 확인
        checkTypeItemId(type, itemId);
        // 좋아요 생성
        Like like = new Like(type, itemId, user);
        // 이미 좋아요있는지 확인
        if (findTypeItemId(type, itemId) == null) {
            likeRepository.save(like);
        } else if (findTypeItemId(type, itemId).getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
        }

        return new LikeResponseDto(like);
    }

    @Transactional
    public void deleteLike(String userEmail, LikeTypeEnum type, Long itemId) {
        // 유저 존재 확인
        findUserEmail(userEmail);
        // type itemId 존재 확인
        checkTypeItemId(type, itemId);
        // 좋아요 삭제
        likeRepository.delete(findTypeItemId(type, itemId));
    }

    public List<LikeResponseDto> getLikes(String userEmail, LikeTypeEnum type) {
        List<Like> likeList;
        if (type == null) {
            // type null 일때 전체 조회
            likeList = likeRepository.findAllByUserOrderByCreateAtDesc(findUserEmail(userEmail));
        } else {
            // type 에 따라 post/comment 다건 조회
            likeList = likeRepository.findAllByUserAndTypeOrderByCreateAtDesc(findUserEmail(userEmail), type);
        }
        return likeList.stream().map(LikeResponseDto::new).toList();
    }


    // type, itemId 로 Like 가져오기 메서드
    public Like findTypeItemId(LikeTypeEnum type, Long itemId) {
        return likeRepository.findByTypeAndItemId(type, itemId);
    }

    // userEmail 로 User 가져오기 메서드
    public User findUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new NullPointerException("없는 유저ID입니다."));
    }

    // type itemID 존재 확인 메서드
    private void checkTypeItemId(LikeTypeEnum type, Long itemId) {
        switch (type) {
            case POST -> {
                postRepository.findById(itemId)
                        .orElseThrow(() -> new NullPointerException("해당 ID의 댓글이 없습니다."));
            }
            case COMMENT -> {
                postCommentRepository.findById(itemId)
                        .orElseThrow(() -> new NullPointerException("해당 ID의 좋아요가 없습니다."));
            }
        }
    }
}
