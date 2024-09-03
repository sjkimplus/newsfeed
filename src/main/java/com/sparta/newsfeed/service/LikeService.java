package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.like.LikeRequestDto;
import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.repository.LikeRepository;
import com.sparta.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponseDto addLike(Long userId, LikeRequestDto requestDto) {
        // 유저 존재 확인
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("없는 유저ID입니다."));
        // 좋아요 생성
        Like like = new Like(requestDto, user);
        likeRepository.save(like);

        return new LikeResponseDto(like, userId);
    }

    @Transactional
    public void deleteLike(Long userId, LikeRequestDto requestDto) {
        // 유저 존재 확인
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("없는 유저ID입니다."));
        // 좋아요 삭제
        likeRepository.delete(likeRepository.findByTypeAndItemIdIn(
                requestDto.getType() ? LikeTypeEnum.Type.POST : LikeTypeEnum.Type.COMMENT,
                requestDto.getItemId()
                ));
    }
}
