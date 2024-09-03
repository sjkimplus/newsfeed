package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.alarm.AlarmRequestDto;
import com.sparta.newsfeed.dto.alarm.AlarmResponseDto;
import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.repository.AlarmRepository;
import com.sparta.newsfeed.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public List<AlarmResponseDto> getAlarms(Long userId) {
        List<Alarm> alarmList = alarmRepository.findAllByUserId(userId);
        return alarmList.stream().map(AlarmResponseDto::new).toList();
    }

    public void deleteAlarm(Long userId, Long alarmId) {
        // 유저 존재 확인
        userRepository.findById(userId).orElseThrow(() -> new NullPointerException("해당 유저ID가 없습니다."));
        // 알림 삭제
        alarmRepository.delete(alarmRepository.findById(alarmId).orElseThrow(() -> new NullPointerException("해당 알림ID가 없습니다")));
    }
}
