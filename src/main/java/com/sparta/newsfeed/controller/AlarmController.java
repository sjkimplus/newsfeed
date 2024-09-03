package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.alarm.AlarmRequestDto;
import com.sparta.newsfeed.dto.alarm.AlarmResponseDto;
import com.sparta.newsfeed.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/api/alarms")
    public List<AlarmResponseDto> getAlarms(@RequestParam("userId") Long userId) {
        return alarmService.getAlarms(userId);
    }

    @DeleteMapping("/api/alarms")
    public void deleteAlarm(
            @RequestParam("userId") Long userId,
            @RequestParam("alarmId") Long alarmId) {
        alarmService.deleteAlarm(userId, alarmId);
    }
}
