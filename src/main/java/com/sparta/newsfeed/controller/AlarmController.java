package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.annotation.Auth;
import com.sparta.newsfeed.dto.AuthUser;
import com.sparta.newsfeed.dto.alarm.AlarmResponseDto;
import com.sparta.newsfeed.dto.alarm.AlarmTextResponseDto;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/api/alarms") // 알림 추가
    public AlarmResponseDto addAlarms(
            @Auth AuthUser authUser,
            @RequestParam("type") AlarmTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        return alarmService.addAlarms(authUser.getEmail(), type, itemId);
    }

    @GetMapping("/api/alarms") // 알림 다건 조회
    public List<AlarmTextResponseDto> getAlarms(@Auth AuthUser authUser) {
        return alarmService.getAlarms(authUser.getEmail());
    }

    @DeleteMapping("/api/alarms") // 알림 삭제
    public void deleteAlarm(@Auth AuthUser authUser, @RequestParam("alarmId") Long alarmId) {
        alarmService.deleteAlarm(authUser.getEmail(), alarmId);
    }
}
