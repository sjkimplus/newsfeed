package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.like.LikeResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import com.sparta.newsfeed.exception.DataDuplicationException;
import com.sparta.newsfeed.exception.DataNotFoundException;
import com.sparta.newsfeed.repository.*;
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
    private final AlarmRepository alarmRepository;

    @Transactional
    public LikeResponseDto addLike(String userEmail, LikeTypeEnum type, Long itemId) {
        // 유저 존재 확인
        User user = findUserEmail(userEmail);
        // type itemId 존재 확인
        User typeIdUser = typeItemId(type, itemId);
        // 좋아요 생성
        Like like = new Like(type, itemId, user);
        // 이미 좋아요있는지 확인
        if (findTypeItemIdUser(type, itemId, user) == null) {
            likeRepository.save(like);
            // 알림 추가
            // 자화자찬 제외
            if(!typeIdUser.equals(user)) {
                sendAlarm(like.getId(), typeIdUser);
            }
            return new LikeResponseDto(like);
        } else if (findTypeItemIdUser(type, itemId, user).getUser().getEmail().equals(userEmail)) {
            throw new DataDuplicationException("이미 좋아요를 눌렀습니다.");
        }
        return null;
    }

    @Transactional
    public String deleteLike(String userEmail, LikeTypeEnum type, Long itemId) {
        // 유저 존재 확인
        User user = findUserEmail(userEmail);
        // type itemId 존재 확인
        typeItemId(type, itemId);
        // 좋아요 검증
        Like like = findTypeItemIdUser(type, itemId, user);
        if (like == null) {
            throw  new DataNotFoundException("삭제할 좋아요가 없습니다.");
        }
        // 좋아요 삭제
        likeRepository.delete(like);
        return "삭제 완료";
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
    public Like findTypeItemIdUser(LikeTypeEnum type, Long itemId, User user) {
        return likeRepository.findByTypeAndItemIdAndUser(type, itemId, user);
    }
    // userEmail 로 User 가져오기 메서드
    public User findUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new DataNotFoundException("선택한 유저는 존재하지 않습니다."));
    }
    // type itemID 존재 확인 메서드
    private User typeItemId(LikeTypeEnum type, Long itemId) {
        switch (type) {
            case POST -> {
                return postRepository.findById(itemId)
                        .orElseThrow(() -> new DataNotFoundException("게시물 " + itemId + " 이 존재하지 않습니다.")).getUser();
            }
            case COMMENT -> {
                return userRepository.findById(
                        postCommentRepository.findById(itemId)
                                .orElseThrow(() -> new DataNotFoundException("댓글 " + itemId + " 이 존재하지 않습니다."))
                                .getUserId()).orElseThrow(() -> new DataNotFoundException("선택한 유저는 존재하지 않습니다."));
            }
            default -> {
                return null;
            }
        }
    }
    // 알림 추가 메서드
    public void sendAlarm(Long itemId, User user) {
        // 유저 존재 확인
        Alarm alarm = new Alarm(AlarmTypeEnum.LIKE, itemId, user);
        // 알림 저장
        alarmRepository.save(alarm);
    }
}
