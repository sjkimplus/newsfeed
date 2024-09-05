package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.alarm.AlarmResponseDto;
import com.sparta.newsfeed.dto.alarm.AlarmTextResponseDto;
import com.sparta.newsfeed.entity.PostComment;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.exception.DataNotFoundException;
import com.sparta.newsfeed.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final RelationshipRepository relationshipRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public AlarmResponseDto addAlarms(String userEmail, AlarmTypeEnum type, Long itemId) {
        // 유저 존재 확인
        findUserEmail(userEmail);
        // 알림 생성
        Alarm alarm = new Alarm(type, itemId, findTypeItemId(type, itemId));
        // type itemID 존재 확인
        findTypeItemId(type, itemId);
        // 알림 저장
        alarmRepository.save(alarm);
        return new AlarmResponseDto(alarm);
    }

    public List<AlarmTextResponseDto> getAlarms(String userEmail) {
        // 본인 확인
        User user = findUserEmail(userEmail);
        // 유저 Id와 일치하는 alarmList 반환
        List<Alarm> alarmList = alarmRepository.findAllByUserIdOrderByIdDesc(user.getId());
        List<AlarmTextResponseDto> dtoList = new ArrayList<>();
        for (Alarm alarm : alarmList) {
            dtoList.add(addAlarmTextDto(alarm));
        }
        return dtoList;
    }

    @Transactional
    public void deleteAlarm(String userEmail, Long alarmId) {
        // 본인 확인
        findUserEmail(userEmail);
        // 알림 삭제
        alarmRepository.delete(alarmRepository.findById(alarmId)
                .orElseThrow(() -> new DataNotFoundException("선택한 알림이 존재하지 않습니다.")));
    }


    public User findUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new DataNotFoundException("선택한 유저가 존재하지 않습니다."));
    }

    // 알림 보낸사람 이름 확인 메서드
    // type itemID 존재 확인
    public User findTypeItemId(AlarmTypeEnum type, Long itemId) {
        return switch (type) {
            case COMMENT -> userRepository.findById(postCommentRepository.findById(itemId)
                            .orElseThrow(() -> new DataNotFoundException("선택한 댓글이 존재하지 않습니다.")).getUserId())
                    .orElseThrow(() -> new DataNotFoundException("선택한 유저가 존재하지 않습니다."));
            case LIKE -> likeRepository.findById(itemId)
                    .orElseThrow(() -> new DataNotFoundException("선택한 좋아요가 존재하지 않습니다.")).getUser();
            case RELATIONSHIP -> relationshipRepository.findById(itemId)
                    .orElseThrow(() -> new DataNotFoundException("선택한 친구 요청이 존재하지 않습니다")).getSentUser();
        };
    }

    // 알림 type 별 textDto 선별
    public AlarmTextResponseDto addAlarmTextDto(Alarm alarm) {
        String type;
        Long itemId;
        switch(alarm.getType()) {
            case LIKE -> { // LIKE & COMMENT 하드 delete시 익셉션남
                Like like = likeRepository.findById(alarm.getItemId())
                        .orElseThrow(() -> new DataNotFoundException("선택한 좋아요가 존재하지 않습니다."));
                type = String.valueOf(like.getType());
                itemId = like.getItemId();
            }
            case COMMENT -> {
                PostComment postComment = postCommentRepository.findById(alarm.getItemId())
                        .orElseThrow(() -> new DataNotFoundException("선택한 댓글이 존재하지 않습니다."));
                type = "POST";
                itemId = postComment.getPost().getId();
            }
            case RELATIONSHIP -> {
                type = "RELATIONSHIP";
                itemId = null;
            }
            default -> {
                type = null;
                itemId = null;
            }
        }
        AlarmTextResponseDto dto = new AlarmTextResponseDto(
                alarm,
                findTypeItemId(alarm.getType(), alarm.getItemId()).getName(),
                type,
                itemId
        );
        return dto;
    }
}
