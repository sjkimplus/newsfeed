package com.sparta.newsfeed.dto.alarm;

import com.sparta.newsfeed.entity.alarm.Alarm;
import com.sparta.newsfeed.entity.like.Like;
import com.sparta.newsfeed.entity.like.LikeTypeEnum;
import lombok.Getter;

@Getter
public class AlarmTextResponseDto {
    private final Long id;
    private final String text;


    public AlarmTextResponseDto(Alarm alarm, String username, Like like) {
        this.id = alarm.getId();
        this.text = alarm.getUser().getName() + "님, "
                + username + "님께서 "
                + alarmTypeStr(like) + " " + alarm.getItemId() + "에 "
                + alarm.getType().name() + "를 추가 하셨습니다.";
    }

    // 어떤곳에 알림이 온건지 알아내는 메서드
    public String alarmTypeStr(Like like) {
        if (like == null) {
            return "POST";
        } else {
            return likeTypeStr(like.getType());
        }
    }

    public String likeTypeStr(LikeTypeEnum type) {
        return switch (type) {
            case POST -> "POST";
            case COMMENT -> "COMMENT";
        };
    }
}
