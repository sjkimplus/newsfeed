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

    @GetMapping("/api/users/{userId}/alarms")
    public List<AlarmResponseDto> getAlarms(@PathVariable Long userId) {
        return alarmService.getAlarms(userId);
    }

    @DeleteMapping("/api/users/{user_id}/like/delete/{alarm_id}")
    public void deleteAlarm(@PathVariable Long user_id, @PathVariable Long alarm_id) {
        alarmService.deleteAlarm(user_id, alarm_id);
    }
}
