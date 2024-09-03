package com.sparta.newsfeed.dto.alarm;

import com.sparta.newsfeed.entity.alarm.Alarm;
import lombok.Getter;

@Getter
public class AlarmTextResponseDto {
    private final Long id;
    private final String text;


    public AlarmTextResponseDto(Alarm alarm, String username) {
        this.id = alarm.getId();
        this.text = alarm.getUser().getName() + "님, " + username + "님께서 " + alarm.getType().name() + "를 추가 하셨습니다.";
    }
}
