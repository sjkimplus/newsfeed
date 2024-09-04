package com.sparta.newsfeed.controller;

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
            @RequestParam("userId") Long userId,
            @RequestParam("type") AlarmTypeEnum type,
            @RequestParam("itemId") Long itemId) {
        return alarmService.addAlarms(userId, type, itemId);
    }

    @GetMapping("/api/alarms") // 알림 다건 조회
    public List<AlarmTextResponseDto> getAlarms(@RequestParam("userId") Long userId) {
        return alarmService.getAlarms(userId);
    }

    @DeleteMapping("/api/alarms") // 알림 삭제
    public void deleteAlarm(@RequestParam("alarmId") Long alarmId) {
        alarmService.deleteAlarm(alarmId);
    }
}
