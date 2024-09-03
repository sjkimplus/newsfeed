package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.LikeRepository;
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

    @Transactional
    public LikeResponseDto addLike(Long userId, LikeTypeEnum type, Long itemId) {
        // 유저 존재 확인
        User user = findUserId(userId);
        // 좋아요 생성
        Like like = new Like(type, itemId, user);
        // 이미 좋아요있는지 확인
        if (findTypeItemId(type, itemId) == null) {
            likeRepository.save(like);
        } else if (findTypeItemId(type, itemId).getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
        }

        return new LikeResponseDto(like);
    }

    @Transactional
    public void deleteLike(Long userId, LikeTypeEnum type, Long itemId) {
        // 유저 존재 확인
        User user = findUserId(userId);
        // 좋아요 삭제
        likeRepository.delete(findTypeItemId(type, itemId));
    }

    public List<LikeResponseDto> getLikes(Long userId, LikeTypeEnum type) {
        List<Like> likeList;
        if (type == null) {
            // type null 일때 전체 조회
            likeList = likeRepository.findAllByUserOrderByCreateAtDesc(findUserId(userId));
        } else {
            // type 에 따라 post/comment 다건 조회
            likeList = likeRepository.findAllByUserAndTypeOrderByCreateAtDesc(findUserId(userId), type);
        }
        return likeList.stream().map(LikeResponseDto::new).toList();
    }


    // type, itemId 로 Like 가져오기 메서드
    public Like findTypeItemId(LikeTypeEnum type, Long itemId) {
        return likeRepository.findByTypeAndItemId(type, itemId);
    }

    // userId로 User 가져오기 메서드
    public User findUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NullPointerException("없는 유저ID입니다."));
    }
}
