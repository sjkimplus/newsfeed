package com.sparta.newsfeed.dto.alarm;

import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.alarm.AlarmTypeEnum;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import lombok.Getter;

@Getter
public class AlarmTextResponseDto {
    private final Long id;
    private final String text;


    public AlarmTextResponseDto(Alarm alarm, String username, String type, Long itemId) {
        this.id = alarm.getId();
        this.text = alarm.getUser().getName() + "님, "
                + username + "님께서 "
                + type + " " + itemId + "에 "
                + alarm.getType().name() + "를 추가 하셨습니다.";
    }
}
