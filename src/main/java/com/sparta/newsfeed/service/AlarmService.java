package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.alarm.AlarmResponseDto;
import com.sparta.newsfeed.dto.alarm.AlarmTextResponseDto;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.repository.AlarmRepository;
import com.sparta.newsfeed.repository.LikeRepository;
import com.sparta.newsfeed.repository.PostCommentRepository;
import com.sparta.newsfeed.repository.UserRepository;
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
    private final LikeRepository likeRepository;

    @Transactional
    public AlarmResponseDto addAlarms(String userEmail, AlarmTypeEnum type, Long itemId) {
        // 유저 존재 확인
        findUserEmail(userEmail);
        // 알림 생성
        Alarm alarm = new Alarm(type, itemId, alarmGetUser(type, itemId));
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
            AlarmTextResponseDto dto = new AlarmTextResponseDto(
                    alarm,
                    alarmGetUser(alarm.getType(), alarm.getItemId()).getName(),
                    findTypeItemId(alarm.getType(), alarm.getItemId())
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public void deleteAlarm(String userEmail, Long alarmId) {
        // 본인 확인
        findUserEmail(userEmail);
        // 알림 삭제
        alarmRepository.delete(alarmRepository.findById(alarmId).orElseThrow(() -> new NullPointerException("해당 ID의 알림이 없습니다")));
    }


    // 유저 존재 확인 메서드
    public User findUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NullPointerException("해당 ID의 유저가 없습니다."));
    }

    public User findUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new NullPointerException("없는 유저ID입니다."));
    }

    // 알림 보낸사람 이름 확인 메서드
    private User alarmGetUser(AlarmTypeEnum type, Long itemId) {
        switch (type) {
            case COMMENT -> {
                return findUserId(
                        postCommentRepository.findById(itemId)
                                .orElseThrow(() -> new NullPointerException("해당 ID의 댓글이 없습니다."))
                                .getUserId());
            }
            case LIKE -> {
                return likeRepository.findById(itemId)
                        .orElseThrow(() -> new NullPointerException("해당 ID의 좋아요가 없습니다."))
                        .getUser();
            }
            default -> {
                return null;
            }
        }
    }

    // type itemID 존재 확인
    private Like findTypeItemId(AlarmTypeEnum type, Long itemId) {
        return switch (type) {
            case COMMENT -> {
                postCommentRepository.findById(itemId)
                        .orElseThrow(() -> new NullPointerException("해당 ID의 댓글이 없습니다."));
                yield null;
            }
            case LIKE -> likeRepository.findById(itemId)
                    .orElseThrow(() -> new NullPointerException("해당 ID의 좋아요가 없습니다."));
        };
    }
}
